package com.github.seungjae97.alyak.alyakapiserver.domain.training.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.TrainingSnapshot;
import com.github.seungjae97.alyak.alyakapiserver.global.common.repository.JDBCRepositoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class TrainingSnapshotRepositoryImpl extends JDBCRepositoryImpl<TrainingSnapshot> {

    public TrainingSnapshotRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected String getInsertSql() {
        return """
                INSERT INTO training_snapshot (
                    training_job_id, image_path, box_index,
                    x_min, y_min, x_max, y_max, created_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
    }

    @Override
    protected Object[] getPreparedStatementParams(TrainingSnapshot snapshot) {
        return new Object[]{
                snapshot.getTrainingJob().getId(),
                snapshot.getImagePath(),
                snapshot.getBoxIndex(),
                snapshot.getXMin(),
                snapshot.getYMin(),
                snapshot.getXMax(),
                snapshot.getYMax(),
                snapshot.getCreatedAt() != null ? snapshot.getCreatedAt() : LocalDateTime.now(),
        };
    }
}
