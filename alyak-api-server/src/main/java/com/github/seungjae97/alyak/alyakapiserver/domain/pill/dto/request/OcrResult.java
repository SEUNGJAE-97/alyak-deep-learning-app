package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request;

public record OcrResult(
        String text,
        float confidence
) {}
