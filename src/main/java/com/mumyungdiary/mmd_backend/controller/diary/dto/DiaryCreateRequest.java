package com.mumyungdiary.mmd_backend.controller.diary.dto;

// DTO
public record DiaryCreateRequest(String content, boolean allowComment, boolean visible) {
}
