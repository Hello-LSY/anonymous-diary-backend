package com.anonymous_diary.ad_backend.repository.diary;

import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Bookmark;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndDiary(User user, Diary diary);

    Slice<Bookmark> findAllByUser(User user, Pageable pageable);
}
