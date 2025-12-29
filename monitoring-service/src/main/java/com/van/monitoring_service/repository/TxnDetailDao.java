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

    public List<TxnDetailDto> findItmxTransaction() {
        String sql = """
            SELECT
                time as time,
                inst_code as inst_code,
                tid as tid,
                trx_resp_cd as trx_resp_cd
            FROM XAPM_TXN_DETAIL
            WHERE 1=1
            ORDER BY time DESC
            LIMIT 100
        """;

        return jdbcTemplate.query(
                sql,
                new BeanPropertyRowMapper<>(TxnDetailDto.class)
        );
    }
}