package com.anonymous_diary.ad_backend.service.diary;


import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.diary.DiaryView;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class DiaryViewService {

    private final DiaryViewRepository diaryViewRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public void markAsViewed(Long userId, Long diaryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));

        Optional<DiaryView> existingView = diaryViewRepository.findByUserAndDiary(user, diary);
        if (existingView.isPresent()) {
            existingView.get().updateViewedAtToNow();
            diaryViewRepository.save(existingView.get()); // 강제 flush로 갱신 반영
        } else {
            diaryViewRepository.save(DiaryView.builder()
                    .user(user)
                    .diary(diary)
                    .viewedAt(LocalDateTime.now())
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public Slice<Long> getViewedDiaryIds(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return diaryViewRepository.findAllByUser(user, pageable)
                .map(view -> view.getDiary().getId());
    }
}

