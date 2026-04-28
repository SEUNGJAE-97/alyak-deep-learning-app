package com.github.seungjae97.alyak.alyakapiserver.domain.training.controller;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.FastApiTrainingClient;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiSystemStatusResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.dto.request.TrainingCompletionCallbackRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.ModelArchiveService;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.TrainingJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/training/jobs")
public class InternalTrainingCallbackController {

    private final TrainingJobService trainingJobService;
    private final ModelArchiveService modelArchiveService;
    private final FastApiTrainingClient fastApiTrainingClient;

    @Value("${training.callback.token:local-training-callback-token}")
    private String callbackToken;

    @Value("${archive.runs-root:./archive-runs}")
    private String archiveRunsRoot;

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
        if (request != null && "SUCCEEDED".equalsIgnoreCase(request.getStatus())) {
            modelArchiveService.importFromRootPath(archiveRunsRoot);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/system-status")
    public ResponseEntity<FastApiSystemStatusResponse> getSystemStatus() {
        try {
            FastApiSystemStatusResponse status = fastApiTrainingClient.getSystemStatus();
            if (status == null) {
                return ResponseEntity.ok(buildOfflineStatus("학습 서버 상태 응답이 비어 있습니다."));
            }
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.ok(buildOfflineStatus("재연결 시도 중..."));
        }
    }

    @GetMapping("/system-status/stream")
    public SseEmitter streamSystemStatus() {
        SseEmitter emitter = new SseEmitter(0L);

        Thread streamThread = new Thread(() -> {
            try {
                while (true) {
                    FastApiSystemStatusResponse status;
                    try {
                        status = fastApiTrainingClient.getSystemStatus();
                        if (status == null) {
                            status = buildOfflineStatus("학습 서버 상태 응답이 비어 있습니다.");
                        }
                    } catch (Exception e) {
                        status = buildOfflineStatus("재연결 시도 중...");
                    }
                    emitter.send(status, MediaType.APPLICATION_JSON);
                    Thread.sleep(7000L);
                }
            } catch (IOException | InterruptedException e) {
                emitter.complete();
            }
        });

        streamThread.setDaemon(true);
        streamThread.start();
        return emitter;
    }

    private FastApiSystemStatusResponse buildOfflineStatus(String message) {
        FastApiSystemStatusResponse status = new FastApiSystemStatusResponse();
        status.setStatus("OFFLINE");
        status.setConnected(false);
        status.setMessage(message);
        status.setDevice("cpu");
        status.setCpuName("알 수 없음");
        status.setRunningJobs(0);
        status.setPendingJobs(0);
        return status;
    }
}
