package com.mumyungdiary.mmd_backend.controller.diary.comment.refine.dto;

import com.mumyungdiary.mmd_backend.domain.common.enums.RefineType;

import jakarta.validation.constraints.Size;

public record RefineRequest(
        RefineType refineType,

        @Size(max = 3000, message = "일기 내용은 최대 3000자까지 다듬을 수 있습니다.")
        String content
) {}
