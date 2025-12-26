package com.van.monitoring_service;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptTest {

    @Test
    void stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("MySecretKey123"); // 🔒 암호화/복호화에 쓸 비밀키 (절대 노출 금지!)
        encryptor.setAlgorithm("PBEWithMD5AndDES");

        String url = "jdbc:clickhouse://192.168.10.55:8123/van_db";
        String username = "admin";
        String password = "V2FuTW9uaXRvcmluZ1N5c3RlbVNlY3JldEtleUZvckpXVFNpZ25pbmc=";

        System.out.println("URL: ENC(" + encryptor.encrypt(url) + ")");
        System.out.println("ID: ENC(" + encryptor.encrypt(username) + ")");
        System.out.println("PW: ENC(" + encryptor.encrypt(password) + ")");
    }
}