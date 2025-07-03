package com.anonymous_diary.ad_backend.service.auth;

import com.anonymous_diary.ad_backend.domain.auth.MagicLinkToken;
import com.anonymous_diary.ad_backend.domain.auth.RefreshToken;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.repository.auth.MagicLinkTokenRepository;
import com.anonymous_diary.ad_backend.repository.auth.RefreshTokenRepository;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MagicLinkTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private static final long REFRESH_EXPIRATION_DAYS = 28;

    @Transactional
    public AuthResponse verifyTokenAndLogin(String token, HttpServletResponse response) {
        MagicLinkToken magicToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

        if (magicToken.isExpired()) {
            throw new InvalidTokenException("토큰이 만료되었습니다.");
        }

        if (magicToken.isUsed()) {
            throw new InvalidTokenException("이미 사용된 토큰입니다.");
        }

        User user = userRepository.findByEmail(magicToken.getEmail())
                .orElseGet(() -> registerNewUser(magicToken.getEmail()));

        String jwt = jwtTokenProvider.generateToken(user.getId());

        refreshTokenRepository.deleteByUser(user);

        String refreshValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_EXPIRATION_DAYS))
                .build();
        refreshTokenRepository.save(refreshToken);

        setRefreshTokenCookie(refreshValue, response);

        magicToken.markAsUsed();

        return new AuthResponse(jwt, user.getId(), user.getNickname());
    }

    @Transactional
    public AuthResponse refresh(String refreshTokenValue, HttpServletResponse response) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByToken(refreshTokenValue);
        if (optional.isEmpty() || optional.get().isExpired()) {
            throw new InvalidTokenException("유효하지 않거나 만료된 Refresh Token 입니다.");
        }

        User user = optional.get().getUser();
        String newAccessToken = jwtTokenProvider.generateToken(user.getId());

        refreshTokenRepository.delete(optional.get());
        String newRefreshValue = UUID.randomUUID().toString();
        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshValue)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(REFRESH_EXPIRATION_DAYS))
                .build();
        refreshTokenRepository.save(newRefreshToken);

        setRefreshTokenCookie(newRefreshValue, response);

        return new AuthResponse(newAccessToken, user.getId(), user.getNickname());
    }

    @Transactional
    public void logoutByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
        refreshTokenRepository.deleteByUser(user);
    }

    private void setRefreshTokenCookie(String value, HttpServletResponse response) {
        String attributes = "HttpOnly; Path=/; Max-Age=" + (REFRESH_EXPIRATION_DAYS * 24 * 60 * 60)
                + "; Secure; SameSite=None";
        response.addHeader("Set-Cookie", "refreshToken=" + value + "; " + attributes);
    }

    private User registerNewUser(String email) {
        int retry = 0;
        while (retry < 5) {
            long count = userRepository.count();
            String nickname = (count + 1) + "번째 무명";
            User newUser = User.create(email, count);
            try {
                return userRepository.save(newUser);
            } catch (DataIntegrityViolationException e) {
                retry++;
            }
        }
        throw new RuntimeException("유저 생성에 실패했습니다. 관리자에게 문의하세요.");
    }

    public record AuthResponse(String accessToken, Long id, String nickname) {}

    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}
