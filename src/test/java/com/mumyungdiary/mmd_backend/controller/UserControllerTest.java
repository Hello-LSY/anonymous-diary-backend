package com.mumyungdiary.mmd_backend.controller;

import com.mumyungdiary.mmd_backend.annotation.WithMockedBeans;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import com.mumyungdiary.mmd_backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockedBeans
class UserControllerTest {

    private static final Long TEST_USER_ID = 1L;
    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Autowired private MockMvc mockMvc;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @MockBean private UserRepository userRepository;

    @Test
    @DisplayName("로그인 사용자 정보 조회에 성공한다.")
    void getCurrentUserInfo_Success() throws Exception {
        User user = new User("test@example.com", "Tester");
        ReflectionTestUtils.setField(user, "id", TEST_USER_ID);
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));

        String token = jwtTokenProvider.generateToken(TEST_USER_ID);

        mockMvc.perform(get("/api/me")
                        .header(AUTH_HEADER, BEARER + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_USER_ID))
                .andExpect(jsonPath("$.nickname").value("Tester"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}
