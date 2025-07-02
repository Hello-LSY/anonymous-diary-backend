package com.anonymous_diary.ad_backend.controller.diary.view;


import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.DiaryViewService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/views")
@RequiredArgsConstructor
public class DiaryViewController {

    private final DiaryViewService diaryViewService;

    //조회기록 갱신
    @PostMapping
    public ResponseEntity<Void> markAsViewed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("diaryId") @NotNull Long diaryId
    ) {
        diaryViewService.markAsViewed(principal.id(), diaryId);
        return ResponseEntity.ok().build();
    }

    //조회
    @GetMapping("/me")
    public ResponseEntity<Slice<Long>> getViewedDiaries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));
        Slice<Long> viewedIds = diaryViewService.getViewedDiaryIds(principal.id(), pageable);
        return ResponseEntity.ok(viewedIds);
    }



}
