package com.mumyungdiary.mmd_backend.config;

import com.mumyungdiary.mmd_backend.service.auth.MagicLinkService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public MagicLinkService magicLinkService() {
        return mock(MagicLinkService.class);
    }

    // 필요 시 다른 공통 mock도 추가
}
