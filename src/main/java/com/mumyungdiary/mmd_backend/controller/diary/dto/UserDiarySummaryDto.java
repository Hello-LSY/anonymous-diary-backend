package com.mumyungdiary.mmd_backend.controller.diary.dto;

import java.time.LocalDateTime;

public record UserDiarySummaryDto(
        Long id,
        String content,
        boolean allowComment,
        boolean visible,
        boolean aiRefined,
        LocalDateTime createdAt
) {}
