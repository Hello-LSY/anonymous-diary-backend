package com.anonymous_diary.ad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anonymous_diary.ad_backend.annotation.WithMockedBeans;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentCreateRequest;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentUpdateRequest;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.repository.diary.CommentRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockedBeans
@Transactional
class CommentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private CommentRepository commentRepository;

    private String jwt;
    private Long userId;
    private Long diaryId;

    @BeforeEach
    void setup() {
        User user = userRepository.save(new User("test@example.com", "무명테스트"));
        userId = user.getId();
        jwt = jwtTokenProvider.generateToken(userId);

        Diary diary = diaryRepository.save(Diary.builder()
                .user(user)
                .content("일기 내용입니다")
                .allowComment(true)
                .visible(true)
                .aiRefined(false)
                .build());
        diaryId = diary.getId();
    }

    @Test
    void 댓글_작성_조회_수정_삭제_성공() throws Exception {
        // 댓글 작성
        CommentCreateRequest createRequest = new CommentCreateRequest("첫 번째 댓글");

        String response = mockMvc.perform(post("/api/comments/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").exists())
                .andReturn().getResponse().getContentAsString();

        Long commentId = objectMapper.readTree(response).get("commentId").asLong();

        // 댓글 조회
        mockMvc.perform(get("/api/comments/" + diaryId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("무명테스트"))
                .andExpect(jsonPath("$[0].content").value("첫 번째 댓글"));


        // 댓글 수정
        CommentUpdateRequest updateRequest = new CommentUpdateRequest("수정된 댓글");
        mockMvc.perform(put("/api/comments/" + commentId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo("수정된 댓글");

        // 댓글 삭제
        mockMvc.perform(delete("/api/comments/" + commentId)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isNoContent());

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }
}
