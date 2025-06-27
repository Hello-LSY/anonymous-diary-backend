package com.mumyungdiary.mmd_backend.controller.user;

import com.mumyungdiary.mmd_backend.controller.user.dto.EmailRequest;
import com.mumyungdiary.mmd_backend.controller.user.dto.LoginRequest;
import com.mumyungdiary.mmd_backend.controller.user.dto.StatusResponse;
import com.mumyungdiary.mmd_backend.controller.user.dto.TokenResponse;
import com.mumyungdiary.mmd_backend.security.jwt.JwtTokenProvider;
import com.mumyungdiary.mmd_backend.service.auth.AuthService;
import com.mumyungdiary.mmd_backend.service.auth.MagicLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final MagicLinkService magicLinkService;

    @PostMapping("/test-login") // 기존 테스트 API
    public ResponseEntity<?> loginTest(@RequestBody LoginRequest request) {
        Long userId = 1L;
        String token = jwtTokenProvider.generateToken(userId);
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        var authResponse = authService.verifyTokenAndLogin(token);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/request-link")
    public ResponseEntity<?> requestMagicLink(@RequestBody EmailRequest request) {
        try {
            magicLinkService.sendLoginLink(request.email());
            return ResponseEntity.ok(new StatusResponse("LINK_SENT"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new StatusResponse("FAILED_TO_SEND"));
        }
    }

}
