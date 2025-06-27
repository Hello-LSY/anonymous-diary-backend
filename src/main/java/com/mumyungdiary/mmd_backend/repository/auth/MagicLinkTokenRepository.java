package com.mumyungdiary.mmd_backend.repository.auth;

import com.mumyungdiary.mmd_backend.domain.auth.MagicLinkToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MagicLinkTokenRepository extends JpaRepository<MagicLinkToken, Long> {
    Optional<MagicLinkToken> findByToken(String token);
}
