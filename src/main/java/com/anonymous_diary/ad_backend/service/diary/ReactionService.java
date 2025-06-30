package com.anonymous_diary.ad_backend.service.diary;

import com.anonymous_diary.ad_backend.controller.diary.reaction.dto.ReactionDto;
import com.anonymous_diary.ad_backend.controller.diary.reaction.dto.ReactionToggleResponse;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.common.enums.ReactionResult;
import com.anonymous_diary.ad_backend.domain.common.enums.ReactionType;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.domain.diary.Reaction;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import com.anonymous_diary.ad_backend.repository.diary.ReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    public ReactionToggleResponse toggleReaction(Long userId, Long diaryId, ReactionType type) {
        Reaction existing = reactionRepository.findByUserIdAndDiaryId(userId, diaryId).orElse(null);
        ReactionResult result;

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
            result = ReactionResult.CREATED;

        } else if (existing.getType() == type) {
            reactionRepository.delete(existing);
            result = ReactionResult.DELETED;

        } else {
            existing.updateType(type);
            result = ReactionResult.UPDATED;
        }

        return new ReactionToggleResponse(result.name());
    }

    @Transactional
    public List<ReactionDto> getReactionsByDiary(Long diaryId) {
        List<Reaction> reactions = reactionRepository.findAllByDiaryId(diaryId);
        return reactions.stream()
                .map(r -> new ReactionDto(r.getUser().getNickname(), r.getType()))
                .collect(Collectors.toList());
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
