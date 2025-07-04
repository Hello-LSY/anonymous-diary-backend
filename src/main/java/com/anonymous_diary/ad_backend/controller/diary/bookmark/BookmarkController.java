package com.anonymous_diary.ad_backend.controller.diary.bookmark;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.BookmarkService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_SIZE;
import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_START;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{diaryId}")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("diaryId") @NotNull Long diaryId
    ) {
        bookmarkService.addBookmark(principal.id(), diaryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable("diaryId") @NotNull Long diaryId
    ) {
        bookmarkService.removeBookmark(principal.id(), diaryId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Slice<VisibleDiarySummaryDto>> getBookmarkedDiaries(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE

            ) int size
    ) {
        Slice<VisibleDiarySummaryDto> bookmarks = bookmarkService.getBookmarkedDiaries(principal.id(), page, size);
        return ResponseEntity.ok(bookmarks);
    }
}
