package com.github.seungjae97.alyak.alyakapiserver.domain.schedule.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "status")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ScheduleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    Long statusId;

    @Column(name = "status_name")
    String statusName;
}
