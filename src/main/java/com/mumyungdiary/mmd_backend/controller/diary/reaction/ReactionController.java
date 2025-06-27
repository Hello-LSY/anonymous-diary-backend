package com.mumyungdiary.mmd_backend.controller.diary.reaction;

import com.mumyungdiary.mmd_backend.controller.diary.reaction.dto.ReactionDto;
import com.mumyungdiary.mmd_backend.controller.diary.reaction.dto.ReactionRequest;
import com.mumyungdiary.mmd_backend.controller.diary.reaction.dto.ReactionToggleResponse;
import com.mumyungdiary.mmd_backend.domain.diary.Reaction;
import com.mumyungdiary.mmd_backend.security.auth.UserPrincipal;
import com.mumyungdiary.mmd_backend.service.diary.ReactionService;
import com.mumyungdiary.mmd_backend.domain.common.enums.ReactionResult;
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
    public ResponseEntity<ReactionToggleResponse> toggleReaction(@PathVariable Long diaryId,
                                                                 @RequestBody ReactionRequest request,
                                                                 @AuthenticationPrincipal UserPrincipal principal) {
        ReactionResult result = reactionService.toggleReaction(principal.id(), diaryId, request.type());
        return ResponseEntity.ok(new ReactionToggleResponse(result.name()));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<List<ReactionDto>> getReactions(@PathVariable Long diaryId) {
        List<Reaction> reactions = reactionService.getReactionsByDiary(diaryId);
        List<ReactionDto> response = reactions.stream()
                .map(r -> new ReactionDto(r.getUser().getNickname(), r.getType()))
                .toList();
        return ResponseEntity.ok(response);
    }

}
