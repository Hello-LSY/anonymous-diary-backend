package com.mumyungdiary.mmd_backend.service.auth;

import com.mumyungdiary.mmd_backend.domain.auth.MagicLinkToken;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.repository.auth.MagicLinkTokenRepository;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import com.mumyungdiary.mmd_backend.security.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MagicLinkTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse verifyTokenAndLogin(String token) {
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

        magicToken.markAsUsed(); // used = true, 영속성 반영

        return new AuthResponse(jwt, user.getId(), user.getNickname());
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
