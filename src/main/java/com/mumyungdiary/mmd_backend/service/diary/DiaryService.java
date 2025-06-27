package com.mumyungdiary.mmd_backend.service.diary;

import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String DIARY_NOT_FOUND = "일기를 찾을 수 없습니다.";
    private static final String UNAUTHORIZED = "해당 일기에 대한 권한이 없습니다.";
    private static final String EDIT_EXPIRED = "작성 후 1시간이 지난 일기는 수정할 수 없습니다.";
    private static final String PRIVATE_DIARY = "비공개 일기입니다.";

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDiary(Long userId, String content, boolean allowComment, boolean visible) {
        User user = getUser(userId);

        Diary diary = Diary.builder()
                .user(user)
                .content(content)
                .allowComment(allowComment)
                .visible(visible)
                .aiRefined(false)
                .build();

        return diaryRepository.save(diary).getId();
    }

    @Transactional(readOnly = true)
    public Diary getDiaryByIdWithAccess(Long diaryId, Long currentUserId) {
        Diary diary = getDiary(diaryId);

        if (!diary.isVisible() && !diary.isOwnedBy(currentUserId)) {
            throw new AccessDeniedException(PRIVATE_DIARY);
        }

        return diary;
    }

    @Transactional(readOnly = true)
    public List<Diary> getDiariesByUser(Long userId) {
        User user = getUser(userId);
        return diaryRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Diary> getPublicDiaries() {
        return diaryRepository.findAllByVisibleTrueOrderByCreatedAtDesc();
    }

    @Transactional
    public void updateDiary(Long diaryId, Long userId, String content, boolean allowComment, boolean visible) {
        Diary diary = getDiary(diaryId);
        validateOwnership(diary, userId);

        if (!diary.isEditable()) {
            throw new IllegalStateException(EDIT_EXPIRED);
        }

        diary.update(content, allowComment, visible);
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long userId) {
        Diary diary = getDiary(diaryId);
        validateOwnership(diary, userId);
        diaryRepository.delete(diary);
    }

    // ===== 내부 헬퍼 =====

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(USER_NOT_FOUND));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException(DIARY_NOT_FOUND));
    }

    private void validateOwnership(Diary diary, Long userId) {
        if (!diary.isOwnedBy(userId)) {
            throw new AccessDeniedException(UNAUTHORIZED);
        }
    }
}
