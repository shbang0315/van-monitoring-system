package com.van.auth_service.controller;

import com.van.auth_service.util.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Data
    static class LoginRequest {
        private String userId;
        private String password;
    }

    @Data
    static class AuthResponse {
        private String token;

        public AuthResponse(String token) {
            this.token = token;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청: {}", request.getUserId());

        try {
            // 1. Spring Security에게 ID/PW 검사를 위임 (가장 중요한 부분!)
            //    -> 내부적으로 CustomUserDetailsService를 호출해서 DB 정보와 비교합니다.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword())
            );

            // 2. 인증 성공 시 사용자 정보(UserDetails)를 꺼냄
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 3. 사용자의 권한(Role) 추출 (예: "ROLE_ADMIN")
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("권한이 없습니다."))
                    .getAuthority();

            // 4. JWT 토큰 발급
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            log.info("로그인 성공! 토큰 발급 완료");
            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패: 비밀번호 불일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 처리 중 오류가 발생했습니다.");
        }
    }
}