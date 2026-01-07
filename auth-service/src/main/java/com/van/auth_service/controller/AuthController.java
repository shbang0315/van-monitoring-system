package com.van.auth_service.controller;

import com.van.auth_service.repository.RefreshTokenRepository;
import com.van.auth_service.util.JwtUtil;
import com.van.auth_service.domain.RefreshToken;
import com.van.auth_service.repository.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

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

    @Data
    static class TokenResponse { // 응답 객체 변경 (Access + Refresh)
        private String accessToken;
        private String refreshToken;

        public TokenResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
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

            // 인증 성공 후
            userDetails = (UserDetails) authentication.getPrincipal();
            role = userDetails.getAuthorities().iterator().next().getAuthority();

            // 1. Access Token 생성 (유효기간 짧게, 예: 30분)
            String accessToken = jwtUtil.generateToken(userDetails.getUsername(), role);
            
            // 2. Refresh Token 생성 (유효기간 길게, 예: 7일) -> JwtUtil에 메서드 추가 필요
            // 임시로 UUID 사용하거나 JwtUtil에 generateRefreshToken 구현 추천
            String refreshTokenValue = jwtUtil.generateToken(userDetails.getUsername(), role); 

            // 3. Redis에 저장 (기존에 있으면 덮어씌움 = 로그인 시마다 갱신)
            RefreshToken refreshToken = new RefreshToken(userDetails.getUsername(), refreshTokenValue);
            refreshTokenRepository.save(refreshToken);

            return ResponseEntity.ok(new AuthResponse(token));

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패: 비밀번호 불일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    // 토큰 재발급 API
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody String requestRefreshToken) {
        // 1. Redis에서 username(key) 등을 통해 토큰 조회 필요하지만, 
        // 보통 클라이언트가 보낸 토큰에서 username을 추출하여 Redis와 비교합니다.
        
        String username = jwtUtil.extractUsername(requestRefreshToken); // JwtUtil 기능 필요
        RefreshToken storedToken = refreshTokenRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("만료된 세션입니다. 다시 로그인해주세요."));

        if (!storedToken.getRefreshToken().equals(requestRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }

        // 2. 새 Access Token 발급
        String newAccessToken = jwtUtil.generateToken(username, "ROLE_ADMIN");

        return ResponseEntity.ok(new TokenResponse(newAccessToken, requestRefreshToken));
    }

}