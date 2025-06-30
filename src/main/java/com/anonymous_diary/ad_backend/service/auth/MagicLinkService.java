package com.anonymous_diary.ad_backend.service.auth;

import com.anonymous_diary.ad_backend.domain.auth.MagicLinkToken;
import com.anonymous_diary.ad_backend.repository.auth.MagicLinkTokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "app.magic-link", name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class MagicLinkService {

    public static final int EXPIRE_TIME = 15;

    private final MagicLinkTokenRepository tokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.magic-link.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendLoginLink(String email) {
        log.info("[MagicLinkService] Magic Link 발급 시도: {}", email);

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRE_TIME);

        MagicLinkToken magicToken = MagicLinkToken.builder()
                .email(email)
                .token(token)
                .expiresAt(expiresAt)
                .used(false)
                .build();

        tokenRepository.save(magicToken);

        String link = baseUrl + "/api/auth/verify?token=" + token;
        log.info("[MagicLinkService] Magic Link 생성 완료: {}", link);

        try {
            sendMail(email, link);
            log.info("[MagicLinkService] 메일 전송 완료: {}", email);
        } catch (MessagingException e) {
            log.error("[MagicLinkService] 메일 전송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메일 전송에 실패했습니다. 잠시 후 다시 시도해 주세요.");
        }
    }

    private void sendMail(String to, String link) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("무명일기 로그인 링크");

        helper.setFrom(fromEmail);

        String html = """
        <p>아래 링크를 클릭하여 무명일기에 로그인하세요 (유효기간: 15분).</p>
        <p><a href="%s">로그인하기</a></p>
        """.formatted(link);

        helper.setText(html, true);
        mailSender.send(message);
    }

}
