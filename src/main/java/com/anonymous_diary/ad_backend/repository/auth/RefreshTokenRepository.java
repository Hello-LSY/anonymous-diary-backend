package com.anonymous_diary.ad_backend.repository.auth;

import com.anonymous_diary.ad_backend.domain.auth.RefreshToken;
import com.anonymous_diary.ad_backend.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}
