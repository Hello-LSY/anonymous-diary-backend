package com.anonymous_diary.ad_backend.domain.diary;

import com.anonymous_diary.ad_backend.domain.auth.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diary_views", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "diary_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DiaryView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Diary diary;

    @Column(nullable = false)
    private LocalDateTime viewedAt;

    public void updateViewedAtToNow() {
        this.viewedAt = LocalDateTime.now();
    }
}
