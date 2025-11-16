package com.github.seungjae97.alyak.alyakapiserver.domain.pill.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.PillSearchRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response.SimplePillInfo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.QPillAppearance.pillAppearance;
import static com.github.seungjae97.alyak.alyakapiserver.domain.pill.entity.QPill.pill;
@Repository
@Transactional
@RequiredArgsConstructor
public class PillRepositoryCustomImpl implements PillRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    private BooleanBuilder buildWhereClause(PillSearchRequest req) {
        BooleanBuilder where = new BooleanBuilder();
        if(req.getShape() != null && !req.getShape().isBlank()) {
            where.and(pillAppearance.pillShapeId.shapeName.eq(req.getShape()));
        }
        if(req.getColor() != null && !req.getColor().isBlank()) {
            where.and(
                pillAppearance.pillColorClass1.colorName.eq(req.getColor())
                .or(pillAppearance.pillColorClass2.colorName.eq(req.getColor()))
            );
        }
        if(req.getScore() != null && !req.getScore().isBlank()) {
            where.and(pillAppearance.pillScore.eq(req.getScore()));
        }
        return where;
    }

    @Override
    public List<SimplePillInfo> searchAppearance(PillSearchRequest pillSearchRequest) {
        BooleanBuilder where = buildWhereClause(pillSearchRequest);
        return queryFactory
                .select(Projections.fields(SimplePillInfo.class,
                        pillAppearance.pillId.as("pillId"),
                        pill.pillName.as("pillName"),
                        pill.pillIngredient.as("ingredient"),
                        pill.pillManufacturer.as("manufacturer")
                ))
                .from(pillAppearance)
                .where(where)
                .fetch();
    }
}
