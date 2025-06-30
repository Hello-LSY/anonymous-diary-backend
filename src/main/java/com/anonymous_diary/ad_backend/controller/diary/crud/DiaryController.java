package com.anonymous_diary.ad_backend.controller.diary.crud;

import com.anonymous_diary.ad_backend.controller.diary.dto.*;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<DiaryCreateResponse> createDiary(
            @RequestBody DiaryCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long diaryId = diaryService.createDiary(
                principal.id(),
                request.content(),
                request.allowComment(),
                request.visible()
        );
        return ResponseEntity.ok(new DiaryCreateResponse(diaryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryDetailDto> getDiary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        DiaryDetailDto response = diaryService.getDiaryDetailWithAccess(id, principal.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<List<UserDiarySummaryDto>> getMyDiaries(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<UserDiarySummaryDto> response = diaryService.getMyDiarySummaries(principal.id());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<List<VisibleDiarySummaryDto>> getPublicDiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<VisibleDiarySummaryDto> response = diaryService.getPublicDiarySummaries(principal.id(), page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDiary(
            @PathVariable Long id,
            @RequestBody DiaryUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        diaryService.updateDiary(id, principal.id(), request.content(), request.allowComment(), request.visible());
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
