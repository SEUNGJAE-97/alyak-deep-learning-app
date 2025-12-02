package com.github.seungjae97.alyak.alyakapiserver.global.common.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseMapper<E, R> {
    R toDto(E entity);

    /**
     * 엔티티 목록을 DTO 목록으로 변환하는 디폴트 메서드
     * @param entities 엔티티 목록
     * @return 변환된 DTO 목록
     */
    default List<R> convertToDtoList(List<E> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}

