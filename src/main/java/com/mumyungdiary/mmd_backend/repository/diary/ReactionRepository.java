package com.mumyungdiary.mmd_backend.repository.diary;

import com.mumyungdiary.mmd_backend.domain.diary.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndDiaryId(Long userId, Long diaryId);
    List<Reaction> findAllByDiaryId(Long diaryId);
}
