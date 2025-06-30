package com.anonymous_diary.ad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anonymous_diary.ad_backend.annotation.WithMockedBeans;
import com.anonymous_diary.ad_backend.controller.user.dto.EmailRequest;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.auth.MagicLinkToken;
import com.anonymous_diary.ad_backend.repository.auth.MagicLinkTokenRepository;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockedBeans
@Transactional
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private MagicLinkTokenRepository tokenRepository;


    @Test
    void 로그인_링크_요청_성공() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com");

        mockMvc.perform(post("/api/auth/request-link")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value("LINK_SENT"));
    }

    @Test
    void 로그인_테스트_토큰_생성() throws Exception {
        String email = "test@example.com";
        String request = """
            {
              "email": "%s"
            }
        """.formatted(email);

        mockMvc.perform(post("/api/auth/test-login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void 로그인_토큰_검증_성공() throws Exception {
        // given
        String email = "verify@example.com";
        String nickname = "1번째 무명";

        User user = userRepository.save(new User(email, nickname));
        String token = UUID.randomUUID().toString();
        tokenRepository.save(MagicLinkToken.builder()
                .email(email)
                .token(token)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build());

        // when & then
        mockMvc.perform(get("/api/auth/verify")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.nickname").value(nickname));
    }
}
