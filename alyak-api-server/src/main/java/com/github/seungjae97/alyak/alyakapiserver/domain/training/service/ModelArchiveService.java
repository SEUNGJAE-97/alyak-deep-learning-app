package com.github.seungjae97.alyak.alyakapiserver.domain.training.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveCompareResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.response.ModelArchiveResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.entity.ModelArchiveStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ModelArchiveService {
    void importFromRootPath(String rootPath);
    Page<ModelArchiveResponse> getArchives(ModelArchiveStatus status, Pageable pageable);
    ModelArchiveDetailResponse getArchive(Long id);
    ModelArchiveCompareResponse compare(Long baseId, Long targetId);
}
