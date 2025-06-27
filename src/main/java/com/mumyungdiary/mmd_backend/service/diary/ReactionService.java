package com.mumyungdiary.mmd_backend.service.diary;

import com.mumyungdiary.mmd_backend.domain.auth.User;
import com.mumyungdiary.mmd_backend.domain.common.enums.ReactionResult;
import com.mumyungdiary.mmd_backend.domain.common.enums.ReactionType;
import com.mumyungdiary.mmd_backend.domain.diary.Diary;
import com.mumyungdiary.mmd_backend.domain.diary.Reaction;
import com.mumyungdiary.mmd_backend.repository.auth.UserRepository;
import com.mumyungdiary.mmd_backend.repository.diary.DiaryRepository;
import com.mumyungdiary.mmd_backend.repository.diary.ReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public ReactionResult toggleReaction(Long userId, Long diaryId, ReactionType type) {
        Reaction existing = reactionRepository.findByUserIdAndDiaryId(userId, diaryId).orElse(null);

        if (existing == null) {
            User user = findUser(userId);
            Diary diary = findDiary(diaryId);
            validateReactionAccess(diary, userId);

            Reaction newReaction = Reaction.builder()
                    .user(user)
                    .diary(diary)
                    .type(type)
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionRepository.save(newReaction);
            return ReactionResult.CREATED;

        } else if (existing.getType() == type) {
            reactionRepository.delete(existing);
            return ReactionResult.DELETED;

        } else {
            existing.updateType(type);
            return ReactionResult.UPDATED;
        }
    }

    @Transactional
    public List<Reaction> getReactionsByDiary(Long diaryId) {
        return reactionRepository.findAllByDiaryId(diaryId);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
    }

    private Diary findDiary(Long id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("일기를 찾을 수 없습니다."));
    }

    private void validateReactionAccess(Diary diary, Long userId) {
        if (!diary.isVisible() && !diary.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("비공개 일기에 리액션을 남길 수 없습니다.");
        }
    }

}
