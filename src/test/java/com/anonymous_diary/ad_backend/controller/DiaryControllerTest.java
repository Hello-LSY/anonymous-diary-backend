package com.anonymous_diary.ad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anonymous_diary.ad_backend.annotation.WithMockedBeans;
import com.anonymous_diary.ad_backend.controller.diary.dto.DiaryCreateRequest;
import com.anonymous_diary.ad_backend.controller.diary.dto.DiaryUpdateRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@WithMockedBeans
class DiaryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private DiaryRepository diaryRepository;

    private String jwt;
    private Long userId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User("test@example.com", "무명테스트"));
        userId = user.getId();
        jwt = jwtTokenProvider.generateToken(userId);
    }

    @Test
    void 일기_CRUD_통합_성공() throws Exception {
        // 1. Create
        DiaryCreateRequest createRequest = new DiaryCreateRequest(
                "일기 작성 테스트", true, true
        );

        String createResponse = mockMvc.perform(post("/api/diaries")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaryId").exists())
                .andReturn().getResponse().getContentAsString();

        Long diaryId = objectMapper.readTree(createResponse).get("diaryId").asLong();

        // 2. Read (상세조회)
        mockMvc.perform(get("/api/diaries/" + diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("일기 작성 테스트"))
                .andExpect(jsonPath("$.title").value("무명테스트"));

        // 3. Update
        DiaryUpdateRequest updateRequest = new DiaryUpdateRequest(
                "수정된 내용", false, false
        );

        mockMvc.perform(put("/api/diaries/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNoContent());

        // 4. Read again to verify update
        mockMvc.perform(get("/api/diaries/" + diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.visible").value(false));

        // 5. Delete
        mockMvc.perform(delete("/api/diaries/" + diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        // 6. Read after delete → 404 or custom 에러 기대
        mockMvc.perform(get("/api/diaries/" + diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().is4xxClientError());
    }
}
