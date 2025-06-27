package com.mumyungdiary.mmd_backend.domain.diary;

import com.mumyungdiary.mmd_backend.domain.auth.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "diaries")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean allowComment;

    @Column(nullable = false)
    private boolean visible;

    @Builder.Default
    private boolean aiRefined = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void update(String content, boolean allowComment, boolean visible) {
        this.content = content;
        this.allowComment = allowComment;
        this.visible = visible;
    }

    public boolean isOwnedBy(Long userId) {
        return this.user.getId().equals(userId);
    }

    public boolean isEditable() {
        return this.createdAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }

}

