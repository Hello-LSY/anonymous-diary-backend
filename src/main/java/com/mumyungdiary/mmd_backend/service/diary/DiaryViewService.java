package com.mumyungdiary.mmd_backend.service.diary;


import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.diary.DiaryView;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryViewRepository;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

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

        boolean alreadyExists = diaryViewRepository.findByUserAndDiary(user, diary).isPresent();
        if (!alreadyExists) {
            diaryViewRepository.save(DiaryView.builder()
                    .user(user)
                    .diary(diary)
                    .build());
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getViewedDiaryIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return diaryViewRepository.findAllByUser(user).stream()
                .map(view -> view.getDiary().getId())
                .toList();
    }
}
