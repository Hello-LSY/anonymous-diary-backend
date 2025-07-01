package com.anonymous_diary.ad_backend.controller.diary.dto;

public record DiaryUpdateRequest(String title, String content, boolean allowComment, boolean visible) {}
