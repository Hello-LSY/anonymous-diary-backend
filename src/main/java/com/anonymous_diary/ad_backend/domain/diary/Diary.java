package com.anonymous_diary.ad_backend.domain.diary;

import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Comment;
import com.anonymous_diary.ad_backend.domain.diary.Reaction;
import com.anonymous_diary.ad_backend.domain.common.enums.ReactionType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 100)
    private String title;

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

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int sadCount = 0;

    @Column(nullable = false)
    private int cheerCount = 0;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

    // ===== 메서드 =====

    public void update(String title, String content, boolean allowComment, boolean visible) {
        this.title = title;
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

    public void markAsAiRefined() {
        this.aiRefined = true;
    }

    public void increaseReaction(ReactionType type) {
        switch (type) {
            case LIKE -> this.likeCount++;
            case SAD -> this.sadCount++;
            case CHEER -> this.cheerCount++;
        }
    }

    public void decreaseReaction(ReactionType type) {
        switch (type) {
            case LIKE -> this.likeCount--;
            case SAD -> this.sadCount--;
            case CHEER -> this.cheerCount--;
        }
    }

    public int getTotalReactionCount() {
        return likeCount + sadCount + cheerCount;
    }
}
