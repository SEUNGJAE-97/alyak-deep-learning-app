package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.Pill;
import com.github.seungjae97.alyak.alyakapiserver.global.common.repository.JDBCRepositoryImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;



@Repository
public class PillRepositoryImpl extends JDBCRepositoryImpl<Pill>{

    public PillRepositoryImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }
    @Override
    protected String getInsertSql() {
        return "INSERT INTO pill (" +
                "pill_id, pill_name, pill_description, user_method, pill_efficacy, " +
                "pill_warn, pill_caution, pill_interactive, pill_adverse_reaction, " +
                "pill_manufacturer, pill_img, pill_ingredient" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected Object[] getPreparedStatementParams(Pill pill) {
        return new Object[] {
                pill.getId(),                    // 1. pill_id
                pill.getPillName(),              // 2. pill_name
                pill.getPillDescription(),       // 3. pill_description
                pill.getUserMethod(),            // 4. user_method
                pill.getPillEfficacy(),          // 5. pill_efficacy
                pill.getPillWarn(),              // 6. pill_warn
                pill.getPillCaution(),           // 7. pill_caution
                pill.getPillInteractive(),       // 8. pill_interactive
                pill.getPillAdverseReaction(),   // 9. pill_adverse_reaction
                pill.getPillManufacturer(),      // 10. pill_manufacturer
                pill.getPillImg(),               // 11. pill_img
                pill.getPillIngredient()         // 12. pill_ingredient
        };
    }
}
