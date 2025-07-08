package com.anonymous_diary.ad_backend.controller.user;

import com.anonymous_diary.ad_backend.controller.user.dto.EmailRequest;
import com.anonymous_diary.ad_backend.controller.user.dto.StatusResponse;
import com.anonymous_diary.ad_backend.security.auth.UserPrincipal;
import com.anonymous_diary.ad_backend.service.auth.AuthService;
import com.anonymous_diary.ad_backend.service.auth.MagicLinkService;
import com.anonymous_diary.ad_backend.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MagicLinkService magicLinkService;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @PostMapping("/request-link")
    public ResponseEntity<StatusResponse> requestMagicLink(@RequestBody EmailRequest request) {
        magicLinkService.sendLoginLink(request.email());
        return ResponseEntity.ok(new StatusResponse("LINK_SENT"));
    }

    @GetMapping("/verify")
    public void verify(@RequestParam String token, HttpServletResponse response) throws IOException {
        var authResponse = authService.verifyTokenAndLogin(token, response);

        String redirectUrl = frontendBaseUrl + "/login/callback"
                + "?accessToken=" + authResponse.accessToken()
                + "&id=" + authResponse.id()
                + "&nickname=" + URLEncoder.encode(authResponse.nickname(), StandardCharsets.UTF_8);

        response.sendRedirect(redirectUrl);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthService.AuthResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        AuthService.AuthResponse authResponse = authService.refresh(refreshToken, response);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<StatusResponse> logout(@AuthenticationPrincipal UserPrincipal principal) {
        authService.logoutByUserId(principal.id());
        return ResponseEntity.ok(new StatusResponse("LOGGED_OUT"));
    }
}
