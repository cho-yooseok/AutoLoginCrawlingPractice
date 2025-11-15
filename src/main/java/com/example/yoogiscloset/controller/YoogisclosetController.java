package com.example.yoogiscloset.controller;

import com.example.yoogiscloset.service.YoogisclosetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.Cookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Tag(name = "Yoogiscloset Auth API", description = "유기스클로젯 로그인 및 세션 관리 API")
@RestController
@RequestMapping("/auth/yoogiscloset")
@RequiredArgsConstructor
public class YoogisclosetController {

    private final YoogisclosetService yoogisclosetService;

    @PostMapping("/session")
    @Operation(summary = "유기스클로젯 로그인 및 세션 쿠키 추출", description = "자동으로 유기스클로젯에 로그인하고, 로그인된 세션의 쿠키를 반환합니다. 판매 등록 API 호출 전에 반드시 실행되어야 합니다.")
    public ResponseEntity<?> getSessionCookies() {
        try {
            Set<Cookie> cookies = yoogisclosetService.loginAndGetCookies();
            return ResponseEntity.ok(cookies);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("세션 쿠키 추출 실패: " + e.getMessage());
        }
    }
}