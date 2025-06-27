package com.mumyungdiary.mmd_backend.controller.diary.reaction.dto;

import com.mumyungdiary.mmd_backend.domain.common.enums.ReactionType;

public record ReactionDto(String nickname, ReactionType type) {
}
