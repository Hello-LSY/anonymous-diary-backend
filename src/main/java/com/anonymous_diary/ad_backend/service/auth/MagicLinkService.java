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

    public void sendLoginLink(String email) throws MessagingException {
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

        sendMail(email, link);
        log.info("[MagicLinkService] 메일 전송 시도 완료");
    }


    private void sendMail(String to, String link) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("무명일기 로그인 링크");

        String html = "<p>아래 링크를 클릭하여 로그인하세요:</p>" +
                "<a href=\"" + link + "\">로그인</a>";

        helper.setText(html, true);
        mailSender.send(message);
    }
}

