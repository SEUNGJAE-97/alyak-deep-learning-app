package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.QSchedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.Schedule;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ScheduleRepositoryCustomImpl implements  ScheduleRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Schedule> findSchedulesByFamilyId(Long familyId) {
        QSchedule schedule = QSchedule.schedule;
        QUser user = QUser.user;
        return queryFactory.selectFrom(schedule)
                .join(schedule.user, user)
                .where(user.family.id.eq(familyId))
                .fetch();
    }
}
