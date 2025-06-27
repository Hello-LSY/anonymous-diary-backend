package com.mumyungdiary.mmd_backend.service.diary;

import com.mumyungdiary.mmd_backend.domain.diary.Comment;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.repository.diary.CommentRepository;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long userId, Long diaryId, String content) {
        User user = findUserById(userId);
        Diary diary = findDiaryById(diaryId);

        if (!diary.isAllowComment()) {
            throw new IllegalStateException("해당 일기는 댓글을 허용하지 않습니다.");
        }

        if (!diary.isVisible() && !diary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("비공개 일기에는 본인만 댓글을 달 수 있습니다.");
        }

        Comment comment = Comment.builder()
                .user(user)
                .diary(diary)
                .content(content)
                .build();

        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public List<Comment> getCommentsByDiary(Long diaryId) {
        Diary diary = findDiaryById(diaryId);
        return commentRepository.findAllByDiaryOrderByCreatedAtAsc(diary);
    }

    @Transactional
    public void updateComment(Long commentId, Long userId, String content) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);
        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        validateCommentOwner(comment, userId);
        commentRepository.delete(comment);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    private Diary findDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NoSuchElementException("댓글을 찾을 수 없습니다."));
    }

    private void validateCommentOwner(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("본인의 댓글만 수정/삭제할 수 있습니다.");
        }
    }
}
