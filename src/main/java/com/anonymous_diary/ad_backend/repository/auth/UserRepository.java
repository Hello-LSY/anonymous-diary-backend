package com.anonymous_diary.ad_backend.repository.auth;


import com.anonymous_diary.ad_backend.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    long count(); // 닉네임 생성을 위해 사용
}
