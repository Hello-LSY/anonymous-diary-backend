package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final DiaryRepository diaryRepository;
    private final DiaryViewRepository diaryViewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<VisibleDiarySummaryDto> getRecentDiaries(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Diary> diaryPage = diaryRepository.findAllByVisibleTrue(pageable);

        Set<Long> viewedIds = new HashSet<>(diaryViewRepository.findViewedDiaryIdsByUserId(userId));

        List<VisibleDiarySummaryDto> content = diaryPage.stream()
                .map(d -> new VisibleDiarySummaryDto(
                        d.getId(),
                        d.getUser().getNickname(),
                        d.getContent(),
                        d.isAllowComment(),
                        d.isAiRefined(),
                        d.getCreatedAt(),
                        viewedIds.contains(d.getId()),
                        d.getTotalReactionCount(),
                        d.getCommentCount()
                ))
                .toList();

        return new PageImpl<>(content, pageable, diaryPage.getTotalElements());
    }
}
