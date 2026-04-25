package com.github.seungjae97.alyak.alyakapiserver.domain.training.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.TrainingCompletionCallbackRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.TrainingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/training/jobs")
public class InternalTrainingCallbackController {

    private final TrainingJobService trainingJobService;

    @Value("${training.callback.token:local-training-callback-token}")
    private String callbackToken;

    @PatchMapping("/{externalJobId}/complete")
    public ResponseEntity<Void> complete(
            @PathVariable String externalJobId,
            @RequestHeader(value = "X-Internal-Token", required = false) String internalToken,
            @RequestBody TrainingCompletionCallbackRequest request
    ) {
        if (internalToken == null || !internalToken.equals(callbackToken)) {
            return ResponseEntity.status(403).build();
        }
        trainingJobService.completeByExternalJobId(externalJobId, request);
        return ResponseEntity.ok().build();
    }
}
