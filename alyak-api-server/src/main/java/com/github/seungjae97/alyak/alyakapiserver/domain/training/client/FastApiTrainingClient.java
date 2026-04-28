package com.github.seungjae97.alyak.alyakapiserver.domain.training.client;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiStartTrainingRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiSystemStatusResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.training.client.dto.FastApiTrainingJobResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FastApiTrainingClient {

    private final RestTemplate restTemplate;

    @Value("${fast-api.server.url:http://localhost:8000}")
    private String fastApiServerUrl;

    public FastApiTrainingJobResponse startTraining(FastApiStartTrainingRequest request) {
        ResponseEntity<FastApiTrainingJobResponse> response = restTemplate.postForEntity(
                fastApiServerUrl + "/train/jobs",
                request,
                FastApiTrainingJobResponse.class
        );
        return response.getBody();
    }

    public FastApiTrainingJobResponse getJobStatus(String externalJobId) {
        return restTemplate.getForObject(
                fastApiServerUrl + "/train/jobs/" + externalJobId,
                FastApiTrainingJobResponse.class
        );
    }

    public FastApiSystemStatusResponse getSystemStatus() {
        return restTemplate.getForObject(
                fastApiServerUrl + "/train/system/status",
                FastApiSystemStatusResponse.class
        );
    }
}
