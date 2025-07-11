package com.anonymous_diary.ad_backend.controller.diary.feed;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_SIZE;
import static com.anonymous_diary.ad_backend.domain.common.constants.PagingConstants.PAGE_START;

@RestController
@RequestMapping("/api/diaries/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    // 무한스크롤
    @GetMapping("/recent")
    public ResponseEntity<Slice<VisibleDiarySummaryDto>> getRecentFeed(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = PAGE_START) int page,
            @RequestParam(defaultValue = PAGE_SIZE) int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Slice<VisibleDiarySummaryDto> feed = feedService.getRecentDiaries(principal.id(), pageable);
        return ResponseEntity.ok(feed);
    }



}
