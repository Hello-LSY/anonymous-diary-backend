package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.dto.VisibleDiarySummaryDto;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Bookmark;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.BookmarkRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public void addBookmark(Long userId, Long diaryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));

        boolean alreadyExists = bookmarkRepository.findByUserAndDiary(user, diary).isPresent();
        if (!alreadyExists) {
            bookmarkRepository.save(Bookmark.builder()
                    .user(user)
                    .diary(diary)
                    .build());
        }
    }

    @Transactional
    public void removeBookmark(Long userId, Long diaryId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));

        bookmarkRepository.findByUserAndDiary(user, diary)
                .ifPresent(bookmarkRepository::delete);
    }

    @Transactional(readOnly = true)
    public Slice<VisibleDiarySummaryDto> getBookmarkedDiaries(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "diary.createdAt"));
        Slice<Bookmark> bookmarks = bookmarkRepository.findAllByUser(user, pageable);

        List<VisibleDiarySummaryDto> content = bookmarks.stream()
                .map(b -> {
                    Diary d = b.getDiary();
                    return new VisibleDiarySummaryDto(
                            d.getId(),
                            d.getTitle(),
                            d.getContent(),
                            d.isAllowComment(),
                            d.isAiRefined(),
                            d.getCreatedAt(),
                            true, // 북마크 목록이므로 viewed = true 로 고정
                            d.getTotalReactionCount(),
                            d.getCommentCount()
                    );
                })
                .toList();

        return new SliceImpl<>(content, pageable, bookmarks.hasNext());
    }
}
