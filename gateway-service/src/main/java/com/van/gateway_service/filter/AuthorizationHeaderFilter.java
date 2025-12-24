package com.van.gateway_service.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

    @Value("${jwt.secret}")
    private String secret;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {
        // 설정이 필요하면 여기에 추가
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. 헤더에 토큰이 있는지 확인
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            // 2. Bearer 토큰 추출
            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");

            // 3. 토큰 유효성 검증
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            // 4. 통과!
            return chain.filter(exchange);
        };
    }

    // JWT 검증 로직
    private boolean isJwtValid(String jwt) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            String subject = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();

            if (subject == null || subject.isEmpty()) {
                return false;
            }
            return true; // 정상 토큰
        } catch (Exception e) {
            log.error("JWT Token validation error: {}", e.getMessage());
            return false; // 위조되거나 만료된 토큰
        }
    }

    // 에러 응답 처리 (Mono 사용 - WebFlux 방식)
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);
        return response.setComplete();
    }
}