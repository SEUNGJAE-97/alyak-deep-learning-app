package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtTokenProvider;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakaoUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.repository.KakaoRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Provider;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.ProviderId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRole;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRoleId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.ProviderRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.RoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import jakarta.transaction.Transactional;
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
    private final ProviderRepository providerRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

    @Value("${KAKAO_CLIENT_SECRET}")
    private String kakaoClientSecret;

    /**
     * 서비스 서버 -> 카카오 서버
     *
     * @param state : uuid
     * @return redirectUrl
     */
    public String buildAuthorizationUrl(String state, String currentBaseUrl) {
        String dynamicRedirectUri = currentBaseUrl + "/auth/kakao/callback";
        OAuthCodeRequest builder = OAuthCodeRequest.builder()
                .clientId(kakaoClientId)
                .redirectUri(dynamicRedirectUri)
                .responseType("code")
                .state(state)
                .build();

        return builder.toUriString(kakaoAuthorUrl);
    }

    @Override
    public String buildAuthorizationUrl(String state) {
        return "";
    }

    /**
     * 서비스 서버 -> 카카오 서버
     *
     * @param code : 카카오 서버에서 주는 code
     * @return token, refresh token 값 반환
     */
    public KakoAuthTokenResponse requestAccessToken(String code, String redirectUri) {
        OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.builder()
                .grant_type("authorization_code")
                .client_id(kakaoClientId)
//                .redirect_uri(kakaoRedirectUri)
                .redirect_uri(redirectUri)
                .code(code)
                .client_secret(kakaoClientSecret)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(oAuthTokenRequest.toMultiValueMap(), headers);

        ResponseEntity<KakoAuthTokenResponse> response =
                restTemplate.postForEntity(kakaoTokenUrl, request, KakoAuthTokenResponse.class);

        // Redis에 토큰 저장 (만료시간 적용)
        kakaoRepository.saveAccessToken(kakaoClientId, response.getBody().getAccess_token(), Long.parseLong(response.getBody().getExpires_in()));

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

    @Transactional
    public TokenResponse saveOrUpdateUser(KakaoUserResponse userInfo) {
        String email = userInfo.getKakaoAccount().getEmail();

        // email 존재 유무 확인
        if (email == null) {
            throw new BusinessException(BusinessError.EMAIL_NOT_EXIST);
        }
        // 만약 신규유저라면
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .name(userInfo.getKakaoAccount().getProfile().getNickname())
                    .build();
            userRepository.save(newUser);

            // 기본 역할 부여
            Role defaultRole = roleRepository.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("기본 역할 정보 없음"));

            UserRoleId userRoleId = new UserRoleId(newUser.getUserId(), defaultRole.getId());
            UserRole userRole = UserRole.builder()
                    .id(userRoleId)
                    .user(newUser)
                    .role(defaultRole)
                    .build();
            userRoleRepository.save(userRole);

            // 제공자 정보 저장
            ProviderId providerId = new ProviderId("KAKAO", newUser.getUserId());
            Provider provider = Provider.builder()
                    .id(providerId)
                    .user(newUser)
                    .build();
            providerRepository.save(provider);

            return newUser;
        });

        String jwtToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .email(user.getEmail())
                .userId(user.getUserId())
                .userName(user.getName())
                .build();
    }

    public boolean validateState(String state) {
        // 서버 세션 또는 DB에 저장된 state 값과 비교해서 검증
        // 여기서는 예시로 항상 true 반환
        return true;
    }
}
