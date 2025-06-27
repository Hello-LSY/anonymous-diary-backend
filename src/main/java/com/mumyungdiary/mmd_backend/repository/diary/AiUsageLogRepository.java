package com.mumyungdiary.mmd_backend.repository.diary;

import com.mumyungdiary.mmd_backend.domain.diary.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {

    @Query("SELECT COUNT(l) FROM AiUsageLog l WHERE l.user.id = :userId AND DATE(l.usedAt) = :date")
    long countByUserIdAndDate(Long userId, LocalDate date);
}
