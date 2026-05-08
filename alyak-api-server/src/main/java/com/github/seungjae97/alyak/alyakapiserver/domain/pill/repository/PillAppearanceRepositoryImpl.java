package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.PillAppearance;
import com.github.seungjae97.alyak.alyakapiserver.global.common.repository.JDBCRepositoryImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PillAppearanceRepositoryImpl extends JDBCRepositoryImpl<PillAppearance> {
    public PillAppearanceRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    @Override
    protected String getInsertSql() {
        return "INSERT INTO pill_appearance (" +
                "pill_id, pill_front, pill_back, pill_classification, pill_type, " +
                "shape_id, color_class1_id, color_class2_id, line_front, line_back, " +
                "mark_code_front_anal, mark_code_back_anal" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected Object[] getPreparedStatementParams(PillAppearance app) {
        return new Object[] {
                app.getPillId(),
                app.getPillFront(),
                app.getPillBack(),
                app.getPillClassification(),
                app.getPillType(),
                app.getShapeId(),
                app.getColorClass1Id(),
                app.getColorClass2Id(),
                app.getLineFront(),
                app.getLineBack(),
                app.getMarkCodeFrontAnal(),
                app.getMarkCodeBackAnal()
        };
    }
}
