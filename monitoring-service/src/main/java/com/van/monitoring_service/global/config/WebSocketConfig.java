package com.van.monitoring_service.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 1. 클라이언트가 연결할 엔드포인트: /ws-monitoring
        registry.addEndpoint("/ws-monitoring")
                .setAllowedOriginPatterns("*") // 모든 출처 허용 (CORS 문제 방지)
                .withSockJS(); // SockJS 지원 (브라우저 호환성)
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 2. 메시지 받을 경로 (구독 Prefix): /topic
        registry.enableSimpleBroker("/topic");
        
        // 3. 메시지 보낼 경로 (발행 Prefix): /app (지금은 안 쓰지만 관례적으로 설정)
        registry.setApplicationDestinationPrefixes("/app");
    }
}