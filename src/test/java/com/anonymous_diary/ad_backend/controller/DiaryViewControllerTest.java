package com.anonymous_diary.ad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anonymous_diary.ad_backend.annotation.WithMockedBeans;
import com.anonymous_diary.ad_backend.controller.diary.dto.DiaryCreateRequest;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@WithMockedBeans
class DiaryViewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private DiaryRepository diaryRepository;

    private String jwt;
    private Long userId;
    private Long diaryId;

    @BeforeEach
    void setup() throws Exception {
        User user = userRepository.save(new User("viewtest@example.com", "뷰테스트"));
        userId = user.getId();
        jwt = jwtTokenProvider.generateToken(userId);

        // 일기 1개 생성
        DiaryCreateRequest createRequest = new DiaryCreateRequest(
                "뷰 기록용 일기", true, true
        );

        String createResponse = mockMvc.perform(post("/api/diaries")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        diaryId = objectMapper.readTree(createResponse).get("diaryId").asLong();
    }

    @Test
    void 뷰_기록_중복방지_조회_통합_성공() throws Exception {
        // 1. 뷰 기록
        mockMvc.perform(post("/api/views")
                        .header("Authorization", "Bearer " + jwt)
                        .param("diaryId", diaryId.toString()))
                .andExpect(status().isOk());

        // 2. 같은 일기로 다시 뷰 기록 (중복 방지)
        mockMvc.perform(post("/api/views")
                        .header("Authorization", "Bearer " + jwt)
                        .param("diaryId", diaryId.toString()))
                .andExpect(status().isOk());

        // 3. 내가 본 일기 목록 조회 → 해당 ID 포함 확인
        String getResponse = mockMvc.perform(get("/api/views/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long viewedId = objectMapper.readTree(getResponse).get(0).asLong();
        assertThat(viewedId).isEqualTo(diaryId);

        // 4. 개수는 1개임 (중복 저장 방지 확인)
        int count = objectMapper.readTree(getResponse).size();
        assertThat(count).isEqualTo(1);
    }
}
