package com.anonymous_diary.ad_backend.controller.diary.crud;

import com.anonymous_diary.ad_backend.controller.diary.dto.*;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_SIZE;
import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_START;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryCreateResponse> createDiary(
            @RequestBody DiaryCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(diaryService.createDiary(principal.id(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryDetailDto> getDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        diaryService.markAsViewed(principal.id(), id);

        return ResponseEntity.ok(diaryService.getDiaryDetailWithAccess(id, principal.id()));
    }

    @GetMapping("/me")
    public ResponseEntity<Slice<UserDiarySummaryDto>> getMyDiaries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<UserDiarySummaryDto> diaries = diaryService.getMyDiarySummaries(principal.id(), pageable);
        return ResponseEntity.ok(diaries);
    }


    @GetMapping("/public")
    public ResponseEntity<Slice<VisibleDiarySummaryDto>> getPublicDiaries(
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE) int size,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<VisibleDiarySummaryDto> diaries = diaryService.getPublicDiarySummaries(principal.id(), pageable);
        return ResponseEntity.ok(diaries);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateDiary(
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        diaryService.patchDiary(id, principal.id(), request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        diaryService.deleteDiary(id, principal.id());
        return ResponseEntity.noContent().build();
    }
}
