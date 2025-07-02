package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.dto.*;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final DiaryViewRepository diaryViewRepository;
    private final DiaryViewService diaryViewService;

    private static final String USER_NOT_FOUND = "사용자를 찾을 수 없습니다.";
    private static final String DIARY_NOT_FOUND = "일기를 찾을 수 없습니다.";
    private static final String UNAUTHORIZED = "해당 일기에 대한 권한이 없습니다.";
    private static final String EDIT_EXPIRED = "작성 후 1시간이 지난 일기는 수정할 수 없습니다.";
    private static final String PRIVATE_DIARY = "비공개 일기입니다.";

    @Transactional
    public DiaryCreateResponse createDiary(Long userId, DiaryCreateRequest request) {
        User user = getUser(userId);
        Diary diary = Diary.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .allowComment(request.allowComment())
                .visible(request.visible())
                .aiRefined(false)
                .build();

        Diary saved = diaryRepository.save(diary);
        return new DiaryCreateResponse(saved.getId());
    }

    @Transactional(readOnly = true)
    public DiaryDetailDto getDiaryDetailWithAccess(Long diaryId, Long currentUserId) {
        Diary diary = getDiary(diaryId);
        if (!diary.isVisible() && !diary.isOwnedBy(currentUserId)) {
            throw new AccessDeniedException(PRIVATE_DIARY);
        }
        return new DiaryDetailDto(
                diary.getId(),
                diary.getUser().getNickname(),
                diary.getTitle(),
                diary.getContent(),
                diary.isAllowComment(),
                diary.isVisible(),
                diary.isAiRefined(),
                diary.getCreatedAt(),
                diary.getLikeCount(),
                diary.getSadCount(),
                diary.getCheerCount(),
                diary.getCommentCount()
        );
    }

    @Transactional(readOnly = true)
    public Slice<UserDiarySummaryDto> getMyDiarySummaries(Long userId, Pageable pageable) {
        User user = getUser(userId);
        Slice<Diary> diaries = diaryRepository.findAllByUser(user, pageable);

        return diaries.map(d -> new UserDiarySummaryDto(
                d.getId(),
                d.getTitle(),
                d.getContent(),
                d.isAllowComment(),
                d.isVisible(),
                d.isAiRefined(),
                d.getCreatedAt(),
                d.getTotalReactionCount(),
                d.getCommentCount()
        ));
    }

    @Transactional(readOnly = true)
    public Slice<VisibleDiarySummaryDto> getPublicDiarySummaries(Long userId, Pageable pageable) {
        Slice<Diary> diaries = diaryRepository.findAllByVisibleTrue(pageable);
        List<Long> viewedIds = diaryViewRepository.findViewedDiaryIdsByUserId(userId);

        return diaries.map(d -> new VisibleDiarySummaryDto(
                d.getId(),
                d.getTitle(),
                d.getContent(),
                d.isAllowComment(),
                d.isAiRefined(),
                d.getCreatedAt(),
                viewedIds.contains(d.getId()),
                d.getTotalReactionCount(),
                d.getCommentCount()
        ));
    }

    @Transactional
    public void updateDiary(Long diaryId, Long userId, DiaryUpdateRequest request) {
        Diary diary = getDiary(diaryId);
        validateOwnership(diary, userId);
        if (!diary.isEditable()) {
            throw new IllegalStateException(EDIT_EXPIRED);
        }
        diary.update(request.title(), request.content(), request.allowComment(), request.visible());
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long userId) {
        Diary diary = getDiary(diaryId);
        validateOwnership(diary, userId);
        diaryRepository.delete(diary);
    }

    @Transactional
    public void markAsViewed(Long userId, Long diaryId) {
        diaryViewService.markAsViewed(userId, diaryId);
    }

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
