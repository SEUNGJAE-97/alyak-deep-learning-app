package com.github.seungjae97.alyak.alyakapiserver.global.common.repository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
public abstract class JDBCRepositoryImpl<T> implements JDBCRepository<T> {

    protected final JdbcTemplate jdbcTemplate;
    protected abstract String getInsertSql();
    protected abstract Object[] getPreparedStatementParams(T entity);

    @Override
    @Transactional
    public void saveAll(List<T> entities) {
        if(entities == null || entities.isEmpty()) return;

        jdbcTemplate.batchUpdate(getInsertSql(), new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Object[] params = getPreparedStatementParams(entities.get(i));
                for (int j = 0; j < params.length; j++) {
                    ps.setObject(j + 1, params[j]);
                }
            }

            @Override
            public int getBatchSize() {
                return entities.size();
            }
        });
    }
}
