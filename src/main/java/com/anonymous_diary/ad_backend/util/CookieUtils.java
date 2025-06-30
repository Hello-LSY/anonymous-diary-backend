package com.anonymous_diary.ad_backend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class CookieUtils {

    private CookieUtils() {
        // 인스턴스화 방지
    }

    public static String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RuntimeException("Refresh Token 쿠키가 없습니다.");
        }
        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException("Refresh Token 쿠키가 없습니다.");
    }
}
