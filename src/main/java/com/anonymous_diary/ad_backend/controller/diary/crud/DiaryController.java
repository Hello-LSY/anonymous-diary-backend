package com.anonymous_diary.ad_backend.controller.diary.crud;

import com.anonymous_diary.ad_backend.controller.diary.dto.*;
import com.mumyungdiary.mmd_backend.controller.diary.dto.*;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
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
    public ResponseEntity<?> createDiary(@RequestBody DiaryCreateRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        Long diaryId = diaryService.createDiary(principal.id(), request.content(), request.allowComment(), request.visible());
        return ResponseEntity.ok(new DiaryCreateResponse(diaryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id,
                                      @AuthenticationPrincipal UserPrincipal principal) {
        Diary diary = diaryService.getDiaryByIdWithAccess(id, principal.id());
        return ResponseEntity.ok(new DiaryDetailDto(
                diary.getId(),
                diary.getUser().getNickname(),
                diary.getContent(),
                diary.isAllowComment(),
                diary.isVisible(),
                diary.isAiRefined(),
                diary.getCreatedAt()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDiaries(@AuthenticationPrincipal UserPrincipal principal) {
        List<Diary> diaries = diaryService.getDiariesByUser(principal.id());

        List<UserDiarySummaryDto> response = diaries.stream()
                .map(d -> new UserDiarySummaryDto(
                        d.getId(),
                        d.getContent(),
                        d.isAllowComment(),
                        d.isVisible(),
                        d.isAiRefined(),
                        d.getCreatedAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicDiaries() {
        List<Diary> diaries = diaryService.getPublicDiaries();

        List<VisibleDiarySummaryDto> response = diaries.stream()
                .map(d -> new VisibleDiarySummaryDto(
                        d.getId(),
                        d.getUser().getNickname(),
                        d.getContent(),
                        d.isAllowComment(),
                        d.isAiRefined(),
                        d.getCreatedAt()
                )).toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiary(@PathVariable Long id,
                                         @RequestBody DiaryUpdateRequest request,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        diaryService.updateDiary(id, principal.id(), request.content(), request.allowComment(), request.visible());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrincipal principal) {
        diaryService.deleteDiary(id, principal.id());
        return ResponseEntity.noContent().build();
    }
}
