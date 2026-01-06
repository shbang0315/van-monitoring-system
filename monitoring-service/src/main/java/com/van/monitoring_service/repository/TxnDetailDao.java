package com.van.monitoring_service.repository;

import com.van.monitoring_service.domain.TxnDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TxnDetailDao {

    private final JdbcTemplate jdbcTemplate;

    public List<TxnDetailDto> findItmxCrdtTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(LOGIN_NAME))[1] as INST_CODE,
                count(*) as COUNT,
                ROUND(
                    countIf(splitByChar('_', assumeNotNull(TX_CODE))[2] != '0000') / count(*) * 100
                , 2) AS REJECT_RATE
            FROM XAPM_TXN_DETAIL A 
            WHERE toDate(TIME) = today()
            AND LENGTH(TX_CODE) = 9 
            AND TX_CODE LIKE '0%_%' 
            AND TX_CODE NOT LIKE '0098_%' 
            AND WAS_ID IN (30081,30001,30002,30091)
            GROUP BY splitByChar('_', assumeNotNull(LOGIN_NAME))[1]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }

    public List<TxnDetailDto> findItmxPontTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(LOGIN_NAME))[1] as INST_CODE,
                count(*) as COUNT,
                ROUND(
                    countIf(splitByChar('_', assumeNotNull(TX_CODE))[2] != '0000') / count(*) * 100
                , 2) AS REJECT_RATE
            FROM XAPM_TXN_DETAIL A 
            WHERE toDate(TIME) = today()
            AND LENGTH(TX_CODE) = 9 
            AND TX_CODE like '1%_%'
            AND WAS_ID in (30081,30001,30002,30091)
            GROUP BY splitByChar('_', assumeNotNull(LOGIN_NAME))[1]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }

    public List<TxnDetailDto> findItmxCashTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(LOGIN_NAME))[1] as INST_CODE,
                count(*) as COUNT,
                ROUND(
                    countIf(splitByChar('_', assumeNotNull(TX_CODE))[2] != '0000') / count(*) * 100
                , 2) AS REJECT_RATE
            FROM XAPM_TXN_DETAIL A 
            WHERE toDate(TIME) = today()
            AND LENGTH(TX_CODE) = 9 
            AND TX_CODE LIKE '0098%_%'
            AND WAS_ID in (30081,30001,30002,30091)
            GROUP BY splitByChar('_', assumeNotNull(LOGIN_NAME))[1]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }

    public List<TxnDetailDto> findItmxCrdtRespTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(TX_CODE))[2] AS TRX_RESP_CD,
                count(*) as COUNT
            FROM XAPM_TXN_DETAIL
            WHERE toDate(TIME) = today()
            AND LENGTH(TX_CODE) = 9 
            AND TX_CODE like '0%_%' 
            AND TX_CODE NOT LIKE '0098_%' 
            AND WAS_ID in (30081,30001,30002,30091)
            GROUP BY splitByChar('_', assumeNotNull(TX_CODE))[2]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }

    public List<TxnDetailDto> findItmxPontRespTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(TX_CODE))[2] AS TRX_RESP_CD,
                count(*) as COUNT
            FROM XAPM_TXN_DETAIL
            WHERE toDate(TIME) = today()
            AND LENGTH(TX_CODE) = 9 
            AND TX_CODE like '1%_%' 
            AND WAS_ID in (30081,30001,30002,30091)
            GROUP BY splitByChar('_', assumeNotNull(TX_CODE))[2]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }

    public List<TxnDetailDto> findItmxCashRespTransaction() {
        String sql = """
            SELECT 
                formatDateTime(MAX(TIME), '%Y-%m-%d %H:%i:%S') AS LAST_TIME,
                splitByChar('_', assumeNotNull(TX_CODE))[2] AS TRX_RESP_CD,
                count(*) as COUNT
            FROM XAPM_TXN_DETAIL
            WHERE toDate(TIME) = today()
            AND WAS_ID in (30081,30001,30002,30091)
            AND LENGTH(TX_CODE) = 9
            AND TX_CODE LIKE '0098%_%'
            GROUP BY splitByChar('_', assumeNotNull(TX_CODE))[2]
            ORDER BY COUNT(*) DESC
            LIMIT 30
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }
}