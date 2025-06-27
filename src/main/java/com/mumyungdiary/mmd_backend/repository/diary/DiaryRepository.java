package com.mumyungdiary.mmd_backend.repository.diary;

import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByUserOrderByCreatedAtDesc(User user);
    List<Diary> findAllByVisibleTrueOrderByCreatedAtDesc();


}
