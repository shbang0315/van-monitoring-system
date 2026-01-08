package com.van.auth_service.controller;

import com.van.auth_service.repository.RefreshTokenRepository;
import com.van.auth_service.util.JwtUtil;
import com.van.auth_service.domain.RefreshToken;
import com.van.auth_service.repository.RefreshTokenRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    @AllArgsConstructor
    @NoArgsConstructor
    static class AuthResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Data
    static class ReissueRequest {
        private String refreshToken;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("로그인 요청: {}", request.getUserId());

        try {
            // 1. Spring Security에게 ID/PW 검사를 위임 (가장 중요한 부분!)
            //    -> 내부적으로 CustomUserDetailsService를 호출해서 계정 정보와 비교합니다.
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
            
            String username = userDetails.getUsername();

            // 두 개의 토큰 생성 (JwtUtil에 해당 메서드가 구현되어 있다고 가정)
            // access token
            String accessToken = jwtUtil.generateAccessToken(username, role); 
            // refresh token
            String refreshTokenValue = jwtUtil.generateRefreshToken(username, role);

            // Refresh Token을 Redis에 저장
            refreshTokenRepository.save(new RefreshToken(username, refreshTokenValue));

            log.info("로그인 성공! Access/Refresh 토큰 발급 완료");
            
            // [수정 4] 두 토큰 모두 반환
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshTokenValue));

        } catch (BadCredentialsException e) {
            log.warn("로그인 실패: 비밀번호 불일치");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        } catch (Exception e) {
            log.error("로그인 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String username) {
        refreshTokenRepository.deleteById(username); // Redis에서 삭제
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 토큰 재발급 API
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody AuthResponse tokenRequest) { // 클라이언트가 가진 RefreshToken을 보냄
        String requestRefreshToken = tokenRequest.getRefreshToken(); // 클라이언트가 보낸 RT

        try {
            // 1. Refresh Token에서 사용자 ID(username) 추출 (JwtUtil 기능 필요)
            // (만약 만료된 토큰이라면 여기서 예외 발생 -> 다시 로그인 하라고 응답)
            String username = jwtUtil.extractUsername(requestRefreshToken);

            // 2. Redis에서 해당 사용자의 저장된 토큰 조회
            // 키 패턴이나 저장 방식에 따라 조회 (여기서는 Repository 사용 가정)
            RefreshToken storedToken = refreshTokenRepository.findById(username)
                    .orElseThrow(() -> new RuntimeException("만료된 세션입니다."));

            // 3. 클라이언트가 보낸 토큰 vs Redis에 있는 토큰 비교
            // (보안: 탈취된 예전 토큰으로 재발급 시도하는 것 방지)
            if (!storedToken.getRefreshToken().equals(requestRefreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
            }

            // 4. 검증 통과! 새로운 Access Token 발급 (Refresh Token은 그대로 유지하거나 갱신 가능)
            // Access Token은 1시간짜리로 다시 줍니다.
            String newAccessToken = jwtUtil.generateAccessToken(username, "ROLE_ADMIN");

            log.info("토큰 재발급 성공: {}", username);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, requestRefreshToken));

        } catch (Exception e) {
            log.error("토큰 재발급 실패", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("재로그인이 필요합니다.");
        }
    }
}