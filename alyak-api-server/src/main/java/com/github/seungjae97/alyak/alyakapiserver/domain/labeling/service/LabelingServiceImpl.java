package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.CreateLabelingItemRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.request.UpdateLabelingBoxesRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.CreateLabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemDetailResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.dto.response.LabelingItemResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.DataStatus;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageBox;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.entity.PillImageData;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageBoxRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.labeling.repository.PillImageDataRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelingServiceImpl implements LabelingService {

    private final PillImageBoxRepository pillImageBoxRepository;
    private final PillImageDataRepository pillImageDataRepository;
    private final LabelingImageStorageService labelingImageStorageService;

    @Override
    public CreateLabelingItemResponse createItem(MultipartFile image, CreateLabelingItemRequest request) {
        String imagePath = labelingImageStorageService.save(image);

        PillImageData imageData = PillImageData.builder()
                .imagePath(imagePath)
                .status(request.getStatus() == null ? DataStatus.INBOX : request.getStatus())
                .build();

        if (request.getBoxes() != null) {
            for (CreateLabelingItemRequest.Box box : request.getBoxes()) {
                PillImageBox imageBox = PillImageBox.builder()
                        .boxIndex(box.getBoxIndex())
                        .xMin(box.getXMin())
                        .yMin(box.getYMin())
                        .xMax(box.getXMax())
                        .yMax(box.getYMax())
                        .build();
                imageData.addBox(imageBox);
            }
        }

        PillImageData saved = pillImageDataRepository.save(imageData);

        return CreateLabelingItemResponse.builder()
                .id(saved.getId())
                .status(saved.getStatus())
                .boxCount(saved.getBoxes().size())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LabelingItemResponse> getItems(DataStatus status, Pageable pageable) {
        Page<PillImageData> page = status == null
                ? pillImageDataRepository.findAll(pageable)
                : pillImageDataRepository.findByStatus(status, pageable);

        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LabelingItemDetailResponse getItemDetail(Long id) {
        PillImageData imageData = pillImageDataRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));

        return toDetailResponse(imageData);
    }

    @Override
    public LabelingItemDetailResponse updateBoxes(Long id, UpdateLabelingBoxesRequest request) {
        PillImageData imageData = pillImageDataRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));

        pillImageBoxRepository.deleteAllByImageDataId(id);

        if (request != null && request.getBoxes() != null) {
            List<UpdateLabelingBoxesRequest.Box> sorted = request.getBoxes().stream()
                    .sorted(Comparator.comparingInt(box -> box.getBoxIndex() == null ? Integer.MAX_VALUE : box.getBoxIndex()))
                    .toList();

            for (int i = 0; i < sorted.size(); i++) {
                UpdateLabelingBoxesRequest.Box box = sorted.get(i);
                imageData.addBox(PillImageBox.builder()
                        .boxIndex(box.getBoxIndex() == null ? i : box.getBoxIndex())
                        .xMin(box.getXMin())
                        .yMin(box.getYMin())
                        .xMax(box.getXMax())
                        .yMax(box.getYMax())
                        .build());
            }
        }

        return toDetailResponse(pillImageDataRepository.save(imageData));
    }

    private LabelingItemDetailResponse toDetailResponse(PillImageData imageData) {
        return LabelingItemDetailResponse.builder()
                .id(imageData.getId())
                .imagePath(imageData.getImagePath())
                .status(imageData.getStatus())
                .createdAt(imageData.getCreatedAt())
                .updatedAt(imageData.getUpdatedAt())
                .boxes(
                        imageData.getBoxes().stream()
                                .map(box -> LabelingItemDetailResponse.Box.builder()
                                        .id(box.getId())
                                        .boxIndex(box.getBoxIndex())
                                        .xMin(box.getXMin())
                                        .yMin(box.getYMin())
                                        .xMax(box.getXMax())
                                        .yMax(box.getYMax())
                                        .build())
                                .toList()
                )
                .build();
    }

    @Override
    public LabelingItemResponse approve(Long id) {
        return updateStatus(id, DataStatus.TRAINING_SET);
    }

    @Override
    public LabelingItemResponse reject(Long id) {
        return updateStatus(id, DataStatus.TRASH);
    }

    private LabelingItemResponse updateStatus(Long id, DataStatus status) {
        PillImageData imageData = pillImageDataRepository.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessError.LABELING_ITEM_NOT_FOUND));

        imageData.updateStatus(status);

        PillImageData saved = pillImageDataRepository.save(imageData);
        return toResponse(saved);
    }

    private LabelingItemResponse toResponse(PillImageData imageData) {
        return LabelingItemResponse.builder()
                .id(imageData.getId())
                .imagePath(imageData.getImagePath())
                .status(imageData.getStatus())
                .boxCount(imageData.getBoxes() == null ? 0 : imageData.getBoxes().size())
                .createdAt(imageData.getCreatedAt())
                .updatedAt(imageData.getUpdatedAt())
                .build();
    }
}
