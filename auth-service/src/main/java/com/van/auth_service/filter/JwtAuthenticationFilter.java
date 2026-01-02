package com.van.auth_service.filter;

import com.van.auth_service.service.CustomUserDetailsService;
import com.van.auth_service.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 헤더에서 "Authorization" 값을 가져옴
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면 통과 (로그인 안 한 상태로 간주)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 이후의 순수 토큰 값만 추출
        jwt = authHeader.substring(7);
        
        try {
            // 4. 토큰에서 사용자 ID 추출
            userId = jwtUtil.extractUsername(jwt);

            // 5. ID가 있고, 현재 SecurityContext에 인증 정보가 없는 경우 (아직 검사 안 함)
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // DB(설정파일)에서 사용자 정보 가져오기
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

                // 6. 토큰이 유효한지 검증
                if (jwtUtil.validateToken(jwt)) {
                    // 7. 유효하면 인증 객체(Authentication) 생성
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 8. SecurityContext에 저장 (이제 Spring Security는 "이 사람 로그인했음"으로 인식)
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("JWT Auth Success for user: {}", userId);
                }
            }
        } catch (Exception e) {
            log.error("JWT Authentication Failed: {}", e.getMessage());
        }

        // 9. 다음 필터로 넘김
        filterChain.doFilter(request, response);
    }
}