package com.mumyungdiary.mmd_backend.controller.diary.dto;

public record DiaryUpdateRequest(String content, boolean allowComment, boolean visible) {}
