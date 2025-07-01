package com.anonymous_diary.ad_backend.controller.diary.dto;

import java.time.LocalDateTime;

public record UserDiarySummaryDto(
        Long id,
        String title,
        String content,
        boolean allowComment,
        boolean visible,
        boolean aiRefined,
        LocalDateTime createdAt,
        int totalReactionCount,
        int commentCount
) {}
