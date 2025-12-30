package com.van.auth_service.controller;

import com.van.auth_service.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@RefreshScope
@Slf4j
public class AuthController {

    private final JwtUtil jwtUtil;

    @Value("${auth.user.username}")
    private String adminUsername;

    @Value("${auth.user.password}")
    private String adminPassword;

    // 로그인 요청 DTO
    @Data
    static class LoginRequest {
        private String userId;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUserId());

        // [수정] 하드코딩 대신 주입받은 변수와 비교
        if (adminUsername.equals(request.getUserId()) && adminPassword.equals(request.getPassword())) {
            
            String token = jwtUtil.generateToken(request.getUserId(), "ADMIN");
            
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);
            response.put("userId", request.getUserId());
            
            log.info("Login successful for user: {}", request.getUserId());
            return ResponseEntity.ok(response);
        } else {
            log.warn("Login failed for user: {}", request.getUserId());
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }
}