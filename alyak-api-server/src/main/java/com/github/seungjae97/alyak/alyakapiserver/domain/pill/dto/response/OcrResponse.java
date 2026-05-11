package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import java.util.List;

public record OcrResponse(
        String shape,
        List<String> texts,
        String color
) {}