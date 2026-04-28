package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service;

import org.springframework.web.multipart.MultipartFile;

public interface LabelingImageStorageService {
    String save(MultipartFile image);
}
