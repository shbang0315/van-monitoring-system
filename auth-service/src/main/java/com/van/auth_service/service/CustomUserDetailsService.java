package com.van.auth_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RefreshScope
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Value("${auth.user.username}")
    private String adminUsername;

    @Value("${auth.user.password}")
    private String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Spring Security: 사용자 정보 조회 시도 - username: {}", username);

        // 1. 설정 파일의 username과 입력받은 username이 같은지 확인
        if (!adminUsername.equals(username)) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
        }

        // 2. 일치한다면 Spring Security가 사용하는 User 객체 생성하여 반환
        // (비밀번호 앞에 {noop}을 붙이면 암호화되지 않은 평문 비밀번호라고 알려주는 것입니다.
        //  추후 DB 도입 시 BCrypt 등으로 변경하면 됩니다.)
        return User.builder()
                .username(adminUsername)
                .password("{noop}" + adminPassword) 
                .roles("ADMIN")
                .build();
    }
}