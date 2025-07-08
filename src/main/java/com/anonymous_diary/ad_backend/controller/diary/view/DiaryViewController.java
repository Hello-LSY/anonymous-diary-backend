package com.anonymous_diary.ad_backend.controller.diary.view;


import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.DiaryViewService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_SIZE;
import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_START;

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
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));
        Slice<Long> viewedIds = diaryViewService.getViewedDiaryIds(principal.id(), pageable);
        return ResponseEntity.ok(viewedIds);
    }

    @GetMapping("/me/details")
    public ResponseEntity<Slice<VisibleDiarySummaryDto>> getRecentlyViewedDiaries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "viewedAt"));
        Slice<VisibleDiarySummaryDto> diaries = diaryViewService.getRecentlyViewedDiaries(principal.id(), pageable);
        return ResponseEntity.ok(diaries);
    }


}
