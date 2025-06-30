package com.anonymous_diary.ad_backend.controller.diary.comment;

import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentCreateRequest;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentCreateResponse;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentDto;
import com.anonymous_diary.ad_backend.controller.diary.comment.dto.CommentUpdateRequest;
import com.anonymous_diary.ad_backend.domain.diary.Comment;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.diary.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{diaryId}")
    public ResponseEntity<CommentCreateResponse> createComment(@PathVariable Long diaryId,
                                                               @Valid @RequestBody CommentCreateRequest request,
                                                               @AuthenticationPrincipal UserPrincipal principal) {
        Long commentId = commentService.createComment(principal.id(), diaryId, request.content());
        return ResponseEntity.ok(new CommentCreateResponse(commentId));
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long diaryId) {
        List<Comment> comments = commentService.getCommentsByDiary(diaryId);
        List<CommentDto> response = comments.stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getUser().getNickname(),
                        c.getContent(),
                        c.getCreatedAt()
                )).toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable Long commentId,
                                              @Valid @RequestBody CommentUpdateRequest request,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        commentService.updateComment(commentId, principal.id(), request.content());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal.id());
        return ResponseEntity.noContent().build();
    }
}
