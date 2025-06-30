package com.anonymous_diary.ad_backend.controller.diary.comment.refine;

import com.anonymous_diary.ad_backend.controller.diary.comment.refine.dto.GeminiRefineResult;
import com.anonymous_diary.ad_backend.controller.diary.comment.refine.dto.RefineRequest;
import com.anonymous_diary.ad_backend.controller.diary.comment.refine.dto.RefineResponse;
import com.anonymous_diary.ad_backend.controller.diary.comment.refine.dto.RefineUpdateRequest;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.ai.GeminiRefineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diaries/{diaryId}/refine")
@RequiredArgsConstructor
public class RefineController {

    private final GeminiRefineService refineService;

    @PostMapping
    public ResponseEntity<RefineResponse> refineDiary(
            @PathVariable Long diaryId,
            @RequestBody RefineRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GeminiRefineResult result = refineService.refineDiary(principal.id(), diaryId, request.refineType());
        return ResponseEntity.ok(new RefineResponse(result.originalContent(), result.refinedContent()));
    }

    @PatchMapping
    public ResponseEntity<Void> updateRefinedDiary(
            @PathVariable Long diaryId,
            @RequestBody RefineUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        refineService.updateRefinedContent(principal.id(), diaryId, request.refinedContent());
        return ResponseEntity.noContent().build();
    }
}
