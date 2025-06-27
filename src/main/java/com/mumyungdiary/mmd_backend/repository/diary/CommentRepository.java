package com.mumyungdiary.mmd_backend.repository.diary;

import com.mumyungdiary.mmd_backend.domain.diary.Comment;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByDiaryOrderByCreatedAtAsc(Diary diary);
}
