package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.repository;

import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.QScheduleBackup;
import com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity.ScheduleBackup;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ScheduleBackupRepositoryCustomImpl implements ScheduleBackupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScheduleBackup> findBackupsByFamilyId(Long familyId) {
        QScheduleBackup backup = QScheduleBackup.scheduleBackup;
        QUser user = QUser.user;
        return queryFactory.selectFrom(backup)
                .join(backup.user, user)
                .where(user.family.id.eq(familyId))
                .orderBy(backup.createdAt.desc())
                .fetch();
    }
}
