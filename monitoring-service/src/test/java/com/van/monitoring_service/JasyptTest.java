package com.van.monitoring_service;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.Test;

public class JasyptTest {

    @Test
    void stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 1. 비밀키 설정 (서버 실행 시 넣을 키와 같아야 함)
        config.setPassword("MySecretKey123");

        // 2. 알고리즘 설정 (강력한 AES-256 사용)
        config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");

        // 3. 해싱 반복 횟수 (기본값 1000)
        config.setKeyObtentionIterations("1000");

        // 4. 인스턴스 풀 크기
        config.setPoolSize("1");

        // 5. Provider 설정
        config.setProviderName("SunJCE");

        // 6. Salt 생성기 클래스
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");

        // 7. [중요] IV 생성기 클래스 (AES 방식엔 필수!)
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");

        // 8. 출력 형태 (Base64)
        config.setStringOutputType("base64");

        encryptor.setConfig(config);

        // --- 암호화할 데이터 입력 ---
        String url = "jdbc:clickhouse://192.168.10.55:8123/van_db";
        String username = "admin";
        String password = "real_password_!@#";
        String jwtSecret = "V2FuTW9uaXRvcmluZ1N5c3RlbVNlY3JldEtleUZvckpXVFNpZ25pbmc=";

        System.out.println("URL: ENC(" + encryptor.encrypt(url) + ")");
        System.out.println("ID: ENC(" + encryptor.encrypt(username) + ")");
        System.out.println("PW: ENC(" + encryptor.encrypt(password) + ")");
        System.out.println("JWT: ENC(" + encryptor.encrypt(jwtSecret) + ")");
    }
}