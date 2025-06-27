package com.mumyungdiary.mmd_backend.repository.diary;

import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.diary.Bookmark;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserAndDiary(User user, Diary diary);
    List<Bookmark> findAllByUser(User user);
}
