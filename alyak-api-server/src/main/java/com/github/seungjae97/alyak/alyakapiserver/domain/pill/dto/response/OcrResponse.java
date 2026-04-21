package com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.response;

import com.github.seungjae97.alyak.alyakapiserver.domain.pill.dto.request.OcrResult;
import java.util.List;

public record OcrResponse(
        String filename,
        List<OcrResult> results,
        String message  // "Success"
) {}