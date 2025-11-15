package com.github.seungjae97.alyak.alyakapiserver.global.http.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestTemplateService {
    private final RestTemplate restTemplate;

    public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        return restTemplate.getForObject(url, responseType, uriVariables);
    }

    public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables) {
        return restTemplate.getForEntity(url, responseType, uriVariables);
    }

    public <T, R> ResponseEntity<T> postForEntity(String url, R request, Class<T> responseType) {
        return restTemplate.postForEntity(url, request, responseType);
    }

    public <T> T getForObject(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }
}
