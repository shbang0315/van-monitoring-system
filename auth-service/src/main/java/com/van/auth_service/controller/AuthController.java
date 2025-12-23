package com.van.auth_service.controller;

import com.van.auth_service.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    // 로그인 요청 DTO
    @Data
    static class LoginRequest {
        private String userId;
        private String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // 1. 아이디/비번 검증 (DB 없이 하드코딩)
        if ("admin".equals(request.getUserId()) && "1234".equals(request.getPassword())) {
            
            // 2. 검증 성공 시 토큰 생성
            String token = jwtUtil.generateToken(request.getUserId(), "ADMIN");
            
            // 3. 응답 반환
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", token);
            response.put("userId", request.getUserId());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }
}