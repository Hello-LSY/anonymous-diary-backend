package com.anonymous_diary.ad_backend.repository.diary;

import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.diary.DiaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DiaryViewRepository extends JpaRepository<DiaryView, Long> {
    Optional<DiaryView> findByUserAndDiary(User user, Diary diary);
    Page<DiaryView> findAllByUser(User user, Pageable pageable);

    @Query("SELECT dv.diary.id FROM DiaryView dv WHERE dv.user.id = :userId")
    List<Long> findViewedDiaryIdsByUserId(Long userId);
}
