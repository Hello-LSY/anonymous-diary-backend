package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentCreateResponse;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentDto;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.diary.Comment;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.CommentRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentCreateResponse createComment(Long userId, Long diaryId, String content) {
        User user = getUser(userId);
        Diary diary = getDiary(diaryId);

        if (!diary.isAllowComment()) {
            throw new IllegalStateException("해당 일기는 댓글을 허용하지 않습니다.");
        }

        if (!diary.isVisible() && !diary.isOwnedBy(userId)) {
            throw new AccessDeniedException("비공개 일기에는 본인만 댓글을 달 수 있습니다.");
        }

        Comment comment = Comment.builder()
                .user(user)
                .diary(diary)
                .content(content)
                .build();

        Long commentId = commentRepository.save(comment).getId();

        diary.increaseCommentCount();
        diaryRepository.save(diary);

        return new CommentCreateResponse(commentId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByDiary(Long diaryId) {
        Diary diary = getDiary(diaryId);
        List<Comment> comments = commentRepository.findAllByDiaryOrderByCreatedAtAsc(diary);

        return comments.stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getUser().getNickname(),
                        c.getContent(),
                        c.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void updateComment(Long commentId, Long userId, String content) {
        Comment comment = getComment(commentId);
        validateOwnership(comment, userId);
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getComment(commentId);
        validateOwnership(comment, userId);

        Diary diary = comment.getDiary();
        commentRepository.delete(comment);

        diary.decreaseCommentCount();
        diaryRepository.save(diary);
    }

    // ===== 내부 헬퍼 메서드 =====

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    private Diary getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));
    }

    private Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));
    }

    private void validateOwnership(Comment comment, Long userId) {
        if (!comment.isOwnedBy(userId)) {
            throw new AccessDeniedException("본인의 댓글만 수정/삭제할 수 있습니다.");
        }
    }
}
