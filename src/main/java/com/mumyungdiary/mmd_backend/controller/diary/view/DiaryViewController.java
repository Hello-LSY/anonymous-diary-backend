package com.mumyungdiary.mmd_backend.controller.diary.view;


import com.mumyungdiary.mmd_backend.security.auth.UserPrincipal;
import com.mumyungdiary.mmd_backend.service.diary.DiaryViewService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/views")
@RequiredArgsConstructor
public class DiaryViewController {

    private final DiaryViewService diaryViewService;

    @PostMapping
    public ResponseEntity<Void> markAsViewed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("diaryId") @NotNull Long diaryId
    ) {
        diaryViewService.markAsViewed(principal.id(), diaryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<Long>> getViewedDiaries(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<Long> viewedIds = diaryViewService.getViewedDiaryIds(principal.id());
        return ResponseEntity.ok(viewedIds);
    }
}
