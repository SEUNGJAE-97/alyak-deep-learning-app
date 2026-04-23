package com.github.seungjae97.alyak.alyakapiserver.domain.labeling.service;

import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LabelingImageStorageServiceImpl implements LabelingImageStorageService {

    private final Path uploadRootPath;

    public LabelingImageStorageServiceImpl(@Value("${app.upload.root-path:./uploads}") String rootPath) {
        this.uploadRootPath = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    @Override
    public String save(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new BusinessException(BusinessError.INVALID_IMAGE_FILE);
        }

        try {
            Path targetDir = uploadRootPath.resolve("pill-images");
            Files.createDirectories(targetDir);

            String extension = getFileExtension(image.getOriginalFilename());
            String fileName = UUID.randomUUID() + (extension.isBlank() ? ".jpg" : "." + extension);
            Path targetPath = targetDir.resolve(fileName);

            Files.copy(image.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/pill-images/" + fileName;
        } catch (IOException e) {
            throw new BusinessException(BusinessError.IMAGE_STORE_FAILED);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}
