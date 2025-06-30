package com.anonymous_diary.ad_backend.controller.diary.dto;

// DTO
public record DiaryCreateRequest(String content, boolean allowComment, boolean visible) {
}
