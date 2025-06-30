package com.anonymous_diary.ad_backend.repository.auth;

import com.anonymous_diary.ad_backend.domain.auth.MagicLinkToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MagicLinkTokenRepository extends JpaRepository<MagicLinkToken, Long> {
    Optional<MagicLinkToken> findByToken(String token);
}
