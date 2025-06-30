package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.diary.DiaryView;

import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final DiaryRepository diaryRepository;
    private final DiaryViewRepository diaryViewRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<VisibleDiarySummaryDto> getRandomRecentDiaries(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Diary> recentDiaries = diaryRepository.findAllByVisibleTrueAndCreatedAtAfter(sevenDaysAgo);

        Set<Long> viewedIds = diaryViewRepository.findAllByUser(user).stream()
                .map(view -> view.getDiary().getId())
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        List<Diary> unviewedDiaries = recentDiaries.stream()
                .filter(d -> !viewedIds.contains(d.getId()))
                .toList();

        List<Diary> targetList = unviewedDiaries.isEmpty() ? recentDiaries : unviewedDiaries;

        if (unviewedDiaries.isEmpty()) {
            diaryViewRepository.deleteAllByUser(user);
        }

        Collections.shuffle(targetList);
        List<Diary> selected = targetList.stream()
                .limit(limit)
                .toList();

        selected.forEach(diary -> {
            boolean alreadyExists = diaryViewRepository.findByUserAndDiary(user, diary).isPresent();
            if (!alreadyExists) {
                diaryViewRepository.save(DiaryView.builder()
                        .user(user)
                        .diary(diary)
                        .build());
            }
        });

        return selected.stream()
                .map(d -> new VisibleDiarySummaryDto(
                        d.getId(),
                        d.getUser().getNickname(),
                        d.getContent(),
                        d.isAllowComment(),
                        d.isAiRefined(),
                        d.getCreatedAt()
                ))
                .toList();
    }
}
