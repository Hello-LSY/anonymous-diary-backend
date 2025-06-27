package com.mumyungdiary.mmd_backend.domain.diary;

import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.common.enums.RefineType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_usage_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Diary diary;

    @Enumerated(EnumType.STRING)
    private RefineType refineType;

    private LocalDateTime usedAt;

    public static AiUsageLog create(User user, Diary diary, RefineType refineType) {
        return AiUsageLog.builder()
                .user(user)
                .diary(diary)
                .refineType(refineType)
                .usedAt(LocalDateTime.now())
                .build();
    }
}
