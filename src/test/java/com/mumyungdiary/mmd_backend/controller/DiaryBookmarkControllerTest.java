package com.mumyungdiary.mmd_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumyungdiary.mmd_backend.annotation.WithMockedBeans;
import com.mumyungdiary.mmd_backend.controller.diary.dto.DiaryCreateRequest;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import com.mumyungdiary.mmd_backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@WithMockedBeans
class DiaryBookmarkControllerTest {

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
        User user = userRepository.save(new User("bookmarktest@example.com", "북마크테스트"));
        userId = user.getId();
        jwt = jwtTokenProvider.generateToken(userId);

        // 일기 1개 생성
        DiaryCreateRequest createRequest = new DiaryCreateRequest(
                "북마크용 일기", true, true
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
    void 북마크_추가_삭제_조회_통합_성공() throws Exception {
        // 1. 북마크 추가
        mockMvc.perform(post("/api/bookmarks/{diaryId}", diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        // 2. 북마크 목록 조회 → 포함 확인
        String getResponse = mockMvc.perform(get("/api/bookmarks/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long bookmarkedId = objectMapper.readTree(getResponse).get(0).asLong();
        assertThat(bookmarkedId).isEqualTo(diaryId);

        // 3. 북마크 삭제
        mockMvc.perform(delete("/api/bookmarks/{diaryId}", diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());

        // 4. 북마크 목록 조회 → 비어있음 확인
        String getResponseAfterDelete = mockMvc.perform(get("/api/bookmarks/me")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(objectMapper.readTree(getResponseAfterDelete).isEmpty()).isTrue();
    }
}
