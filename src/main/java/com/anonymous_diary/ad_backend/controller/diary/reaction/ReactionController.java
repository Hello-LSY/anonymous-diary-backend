package com.anonymous_diary.ad_backend.controller.diary.reaction;

import com.anonymous_diary.ad_backend.controller.diary.reaction.dto.ReactionRequest;
import com.anonymous_diary.ad_backend.controller.diary.reaction.dto.ReactionToggleResponse;
import com.anonymous_diary.ad_backend.controller.diary.reaction.dto.ReactionDto;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/{diaryId}")
    public ResponseEntity<ReactionToggleResponse> toggleReaction(
            @PathVariable Long diaryId,
            @RequestBody ReactionRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(reactionService.toggleReaction(principal.id(), diaryId, request.type()));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<List<ReactionDto>> getReactions(@PathVariable Long diaryId) {
        return ResponseEntity.ok(reactionService.getReactionsByDiary(diaryId));
    }
}
