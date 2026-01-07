package com.van.auth_service.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

// redis에 "refreshToken"이라는 키 prefix로 저장, 86400초(1일) 뒤 자동 삭제
@RedisHash(value = "refreshToken", timeToLive = 86400)
public class RefreshToken {
    @Id
    private String username; // key로 사용할 사용자 ID (admin)
    private String refreshToken;

    public RefreshToken(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }
    
    // Getter, Setter, NoArgsConstructor 등 필요 시 추가
    public String getRefreshToken() { 
        return refreshToken; 
    }
}