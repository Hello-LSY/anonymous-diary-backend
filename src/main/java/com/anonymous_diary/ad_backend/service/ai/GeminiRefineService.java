package com.anonymous_diary.ad_backend.service.ai;

import com.anonymous_diary.ad_backend.controller.diary.comment.refine.dto.GeminiRefineResult;
import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.domain.common.enums.RefineType;
import com.anonymous_diary.ad_backend.domain.diary.AiUsageLog;
import com.anonymous_diary.ad_backend.domain.diary.Diary;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.repository.diary.AiUsageLogRepository;
import com.anonymous_diary.ad_backend.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.anonymous_diary.ad_backend.domain.common.constants.AiConstants.DAILY_REFINE_LIMIT;


import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiRefineService {

    private static final String ERR_DIARY_NOT_FOUND = "일기를 찾을 수 없습니다.";
    private static final String ERR_DIARY_FORBIDDEN = "본인의 일기만 다듬을 수 있습니다.";
    private static final String ERR_LIMIT_EXCEEDED = "AI 다듬기는 하루 최대 5회까지만 가능합니다.";
    private static final String ERR_AI_FAILED = "AI 처리 도중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final AiUsageLogRepository aiUsageLogRepository;
    private final ChatClient chatClient;
    private final GeminiPromptFactory promptFactory;


    @Transactional
    public GeminiRefineResult refineDiary(Long userId, Long diaryId, RefineType type) {
        checkUsageLimit(userId);

        Diary diary = getOwnedDiary(userId, diaryId);
        List<Message> prompt = promptFactory.createPrompt(diary.getContent(), type);

        String refinedContent;
        try {
            refinedContent = chatClient.prompt()
                    .messages(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new IllegalStateException(ERR_AI_FAILED);
        }

        // 기록 저장
        aiUsageLogRepository.save(AiUsageLog.create(diary.getUser(), diary, type));

        return new GeminiRefineResult(diary.getContent(), refinedContent);
    }

  
    @Transactional
    public GeminiRefineResult refineContentDuringWriting(Long userId, String content, RefineType type) {
        checkUsageLimit(userId);

        List<Message> prompt = promptFactory.createPrompt(content, type);

        String refinedContent;
        try {
            refinedContent = chatClient.prompt()
                    .messages(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new IllegalStateException(ERR_AI_FAILED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        aiUsageLogRepository.save(AiUsageLog.createDuringWriting(user, type));

        return new GeminiRefineResult(content, refinedContent);
    }


    @Transactional
    public void updateRefinedContent(Long userId, Long diaryId, String refinedContent) {
        Diary diary = getOwnedDiary(userId, diaryId);
        diary.updateContent(refinedContent);
        diary.markAsAiRefined();
    }

    private void checkUsageLimit(Long userId) {
        long usageCount = aiUsageLogRepository.countByUserIdAndDate(userId, LocalDate.now());
        if (usageCount >= DAILY_REFINE_LIMIT) {
            throw new IllegalStateException(ERR_LIMIT_EXCEEDED);
        }
    }

    private Diary getOwnedDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NoSuchElementException(ERR_DIARY_NOT_FOUND));

        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException(ERR_DIARY_FORBIDDEN);
        }
        return diary;
    }

    @Transactional(readOnly = true)
    public int getTodayUsageCount(Long userId) {
        return (int) aiUsageLogRepository.countByUserIdAndDate(userId, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public int getRemainingUsageCount(Long userId) {
        return DAILY_REFINE_LIMIT - getTodayUsageCount(userId);
    }

}
