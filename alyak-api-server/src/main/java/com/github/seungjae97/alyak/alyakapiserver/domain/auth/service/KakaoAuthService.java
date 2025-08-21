package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.KakaoAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.repository.KakaoRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoAuthService implements OAuthService {

    private final KakaoRepository kakaoRepository;
    private final UserRepository userRepository;

    @Value("${KAKAO_AUTHORIZE_URL}")
    private String kakaoAuthorUrl;

    @Value("${KAKAO_TOKEN_URL}")
    private String kakaoTokenUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${KAKAO_REDIRECT_URI}")
    private String kakaoRedirectUri;


    @Value("${KAKAO_REST_API_KEY}")
    private String kakaoClientId;

    /**
     * 서비스 서버 -> 카카오 서버
     *
     * @param state : uuid
     * @return redirectUrl
     */
    public String buildAuthorizationUrl(String state) {
        OAuthCodeRequest builder = OAuthCodeRequest.builder()
                .clientId(kakaoClientId)
                .redirectUri(kakaoRedirectUri)
                .responseType("code")
                .state(state)
                .build();

        return builder.toUriString(kakaoAuthorUrl);
    }

    /**
     * 서비스 서버 -> 카카오 서버
     *
     * @param code : 카카오 서버에서 주는 code
     * @return token, refresh token 값 반환
     */
    public KakoAuthTokenResponse requestAccessToken(String code) {
        KakaoAuthTokenRequest kakaoAuthTokenRequest = KakaoAuthTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(kakaoClientId)
                .redirect_uri(kakaoRedirectUri)
                .code(code)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(kakaoAuthTokenRequest.toMultiValueMap(), headers);

        ResponseEntity<KakoAuthTokenResponse> response =
                restTemplate.postForEntity(kakaoTokenUrl, request, KakoAuthTokenResponse.class);

        // Redis에 토큰 저장 (만료시간 적용)
        kakaoRepository.saveAccessToken(kakaoClientId, response.getBody().getAccess_token(), Long.parseLong(response.getBody().getExpires_in()));

        //log.info(response.getBody().getAccess_token());

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
