package com.anonymous_diary.ad_backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NeonKeepAliveScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedDelay = 240_000) // 4분마다 실행
    public void keepNeonAwake() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("[NeonKeepAliveScheduler] Successfully pinged Neon.");
        } catch (Exception e) {
            log.error("[NeonKeepAliveScheduler] Failed to ping Neon: {}", e.getMessage(), e);
        }
    }
}
