package com.anonymous_diary.ad_backend.repository.diary;

import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByUserOrderByCreatedAtDesc(User user);
    List<Diary> findAllByVisibleTrueOrderByCreatedAtDesc();
    List<Diary> findAllByVisibleTrueAndCreatedAtAfter(LocalDateTime createdAt);


}
