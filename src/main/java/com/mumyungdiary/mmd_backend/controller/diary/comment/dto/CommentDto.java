package com.mumyungdiary.mmd_backend.controller.diary.comment.dto;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String nickname,
        String content,
        LocalDateTime createdAt
) {}
