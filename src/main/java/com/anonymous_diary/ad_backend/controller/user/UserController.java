package com.anonymous_diary.ad_backend.controller.user;

import com.anonymous_diary.ad_backend.domain.auth.User;
import com.anonymous_diary.ad_backend.repository.auth.UserRepository;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.id())
                .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
        return ResponseEntity.ok(new UserInfoResponse(user.getId(), user.getNickname(), user.getEmail()));
    }

    public record UserInfoResponse(Long id, String nickname, String email) {}
}
