package com.github.seungjae97.alyak.alyakapiserver.global.common.repository;

import java.util.List;

public interface JDBCRepository<T> {
    void saveAll(List<T> entities);
}
