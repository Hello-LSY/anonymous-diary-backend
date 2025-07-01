package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final DiaryRepository diaryRepository;
    private final DiaryViewRepository diaryViewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<VisibleDiarySummaryDto> getRecentDiaries(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Diary> diaryPage = diaryRepository.findAllByVisibleTrue(pageable);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        Set<Long> viewedIds = diaryViewRepository.findAllByUser(user).stream()
                .map(dv -> dv.getDiary().getId())
                .collect(Collectors.toSet());

        return diaryPage.stream()
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
    }
}
