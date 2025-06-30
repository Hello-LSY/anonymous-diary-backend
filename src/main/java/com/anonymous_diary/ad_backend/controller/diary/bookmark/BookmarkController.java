package com.anonymous_diary.ad_backend.controller.diary.bookmark;

import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.BookmarkService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Long>> getBookmarks(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<Long> bookmarks = bookmarkService.getBookmarkedDiaryIds(principal.id());
        return ResponseEntity.ok(bookmarks);
    }
}
