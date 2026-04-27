package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.CreateLabelingItemRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.UpdateLabelingBoxesRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.CreateLabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LabelingService {
    CreateLabelingItemResponse createItem(MultipartFile image, CreateLabelingItemRequest request);
    Page<LabelingItemResponse> getItems(DataStatus status, Pageable pageable);
    LabelingItemDetailResponse getItemDetail(Long id);
    LabelingItemDetailResponse updateBoxes(Long id, UpdateLabelingBoxesRequest request);
    LabelingItemResponse approve(Long id);
    LabelingItemResponse reject(Long id);
    LabelingItemResponse updateStatus(Long id, DataStatus status);
    List<LabelingItemResponse> updateStatuses(List<Long> ids, DataStatus status);
}
