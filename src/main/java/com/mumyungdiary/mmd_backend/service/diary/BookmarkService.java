package com.mumyungdiary.mmd_backend.service.diary;

import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.diary.Bookmark;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import com.mumyungdiary.mmd_backend.repository.diary.BookmarkRepository;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
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
    public List<Long> getBookmarkedDiaryIds(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        return bookmarkRepository.findAllByUser(user).stream()
                .map(b -> b.getDiary().getId())
                .toList();
    }
}
