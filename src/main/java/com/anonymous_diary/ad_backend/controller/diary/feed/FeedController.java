package com.anonymous_diary.ad_backend.controller.diary.feed;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/random")
    public ResponseEntity<List<VisibleDiarySummaryDto>> getRandomFeed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(value = "limit", defaultValue = "9") int limit
    ) {
        List<VisibleDiarySummaryDto> feed = feedService.getRandomRecentDiaries(principal.id(), limit);
        return ResponseEntity.ok(feed);
    }
}
