package com.mumyungdiary.mmd_backend.controller.diary.dto;

import java.time.LocalDateTime;

public record DiaryDetailDto(
        Long id,
        String nickname,
        String content,
        boolean allowComment,
        boolean visible,
        boolean aiRefined,
        LocalDateTime createdAt
) {}
