package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.repository.KakaoRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoRepository kakaoRepository;
    private final UserRepository userRepository;
    private final String baseAuthorizeUrl = "https://kauth.kakao.com/oauth/authorize";
    private final String baseTokenUrl = "https://kauth.kakao.com/oauth/token";
    @Autowired
    private RestTemplate restTemplate;

    @Value("${REDIRECT_URI}")
    private String redirectUri;

    @Value("${KAKAO_REST_API_KEY}")
    private String clientId;

    public String buildAuthorizationUrl(String state) {
        KakaoAuthCodeRequest request = KakaoAuthCodeRequest.builder()
                .clientId(clientId)
                .redirectUri(redirectUri)
                .responseType("code")
                .state(state)
                .build();

        return request.toUriString(baseAuthorizeUrl);
    }

    public KakoAuthTokenResponse requestAccessToken(String code) {
        KakaoAuthTokenRequest kakaoAuthTokenRequest = KakaoAuthTokenRequest.builder()
                .grant_type("authorization_code")
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

    public KakaoUserResponse requestUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoUserResponse.class);

        return response.getBody();
    }

    public Boolean saveOrUpdateUser(KakaoUserResponse userInfo) {
        // email 존재 유무 확인
        if (userInfo.getKakaoAccount().getEmail() == null) {
            return false;
        }
        // 이미 가입한 사람인지 확인
        if (userRepository.existsByEmail(userInfo.getKakaoAccount().getEmail())) {
            return false;
        }
        //신규 가입 처리
        User newUser = User.builder()
                .email(userInfo.getKakaoAccount().getEmail())
                .id(userInfo.getId())
                .name(userInfo.getKakaoAccount().getProfile().getNickname())
                .build();
        userRepository.save(newUser);
        return true;
    }

    public boolean validateState(String state) {
        // 서버 세션 또는 DB에 저장된 state 값과 비교해서 검증
        // 여기서는 예시로 항상 true 반환
        return true;
    }
}
