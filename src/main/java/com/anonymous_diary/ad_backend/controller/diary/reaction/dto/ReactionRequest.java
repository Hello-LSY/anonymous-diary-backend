package com.anonymous_diary.ad_backend.controller.diary.reaction.dto;

import com.anonymous_diary.ad_backend.domain.common.enums.ReactionType;

public record ReactionRequest(ReactionType type) {
}
