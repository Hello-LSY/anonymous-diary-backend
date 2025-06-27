package com.mumyungdiary.mmd_backend.repository.diary;


import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.diary.DiaryView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryViewRepository extends JpaRepository<DiaryView, Long> {
    Optional<DiaryView> findByUserAndDiary(User user, Diary diary);

    List<DiaryView> findAllByUser(User user);
}
