package com.example.SomeOne.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class SecurityUtil {

    public static Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("Authenticated user not found");
        }

        Object principal = authentication.getPrincipal();

        // principal이 UserDetails 객체인지 확인
        if (principal instanceof User) {
            User userDetails = (User) principal;
            try {
                return Long.valueOf(userDetails.getUsername()); // username을 Long으로 변환
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("User ID is not a valid number");
            }
        }
        // principal이 문자열일 경우
        else if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal); // principal이 숫자형 문자열일 경우 처리
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("User ID is not a valid number");
            }
        }

        throw new IllegalArgumentException("Unknown principal type");
    }
}
