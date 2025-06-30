package com.anonymous_diary.ad_backend.repository.diary;

import com.anonymous_diary.ad_backend.domain.diary.Comment;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByDiaryOrderByCreatedAtAsc(Diary diary);
}
