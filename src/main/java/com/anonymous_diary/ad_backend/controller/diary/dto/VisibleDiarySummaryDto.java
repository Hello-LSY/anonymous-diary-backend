package com.anonymous_diary.ad_backend.controller.diary.dto;

import java.time.LocalDateTime;

public record VisibleDiarySummaryDto(
        Long id,
        String nickname,
        String content,
        boolean allowComment,
        boolean aiRefined,
        LocalDateTime createdAt
) {
}
