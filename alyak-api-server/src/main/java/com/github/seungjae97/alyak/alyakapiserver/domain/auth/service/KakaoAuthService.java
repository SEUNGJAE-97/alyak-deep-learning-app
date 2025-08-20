package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.repository.KakaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoRepository kakaoRepository;
    private final String clientId = "YOUR_REST_API_KEY";
    private final String baseAuthorizeUrl = "https://kauth.kakao.com/oauth/authorize";
    private final String baseTokenUrl = "https://kauth.kakao.com/oauth/token";
    @Autowired
    private RestTemplate restTemplate;

    public String buildAuthorizationUrl(String redirectUri, String state) {
        KakaoAuthCodeRequest request = KakaoAuthCodeRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType("code")
                .state(state)
                .build();

        return request.toUriString(baseAuthorizeUrl);
    }

    public KakoAuthTokenResponse requestAccessToken(String code, String redirectUri){
        KakaoAuthTokenRequest kakaoAuthTokenRequest = KakaoAuthTokenRequest.builder()
                .authorization_code("authorization_code")
                .client_id(clientId)
                .redirect_uri(redirectUri)
                .code(code)
                //.client_secret(clientSecret)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(kakaoAuthTokenRequest.toMultiValueMap(), headers);

        ResponseEntity<KakoAuthTokenResponse> response =
                restTemplate.postForEntity(baseTokenUrl, request, KakoAuthTokenResponse.class);

        // Redis에 토큰 저장 (만료시간 적용)
        kakaoRepository.saveAccessToken(clientId, response.getBody().getAccess_token(), Long.parseLong(response.getBody().getExpires_in()));
        return response.getBody();
    }
}
