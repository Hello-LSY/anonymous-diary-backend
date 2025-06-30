package com.anonymous_diary.ad_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.anonymous_diary.ad_backend.annotation.WithMockedBeans;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.diary.Reaction;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.ReactionRepository;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@WithMockedBeans
class ReactionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserRepository userRepository;
    @Autowired private DiaryRepository diaryRepository;
    @Autowired private ReactionRepository reactionRepository;

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
                .content("리액션 테스트 일기")
                .allowComment(true)
                .visible(true)
                .aiRefined(false)
                .build());
        diaryId = diary.getId();
    }

    @Test
    void 반응_등록_중복제거_다른타입변경_성공() throws Exception {
        // 좋아요 등록
        mockMvc.perform(post("/api/reactions/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"LIKE\"}"))
                .andExpect(status().isOk());

        assertThat(reactionRepository.findAll()).hasSize(1);
        Reaction reaction = reactionRepository.findAll().get(0);
        assertThat(reaction.getType().name()).isEqualTo("LIKE");

        // 좋아요 다시 클릭 → 삭제됨
        mockMvc.perform(post("/api/reactions/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"LIKE\"}"))
                .andExpect(status().isOk());

        assertThat(reactionRepository.findAll()).isEmpty();

        // 다시 등록 (LIKE), 그리고 다른 타입 (SAD)로 변경
        mockMvc.perform(post("/api/reactions/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"LIKE\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/reactions/" + diaryId)
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"SAD\"}"))
                .andExpect(status().isOk());

        assertThat(reactionRepository.findAll()).hasSize(1);
        Reaction updatedReaction = reactionRepository.findAll().get(0);
        assertThat(updatedReaction.getType().name()).isEqualTo("SAD");
    }
}
