package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtTokenProvider;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.GoogleUserResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRole;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRoleId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.RoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService implements OAuthService {


    @Value("${GOOGLE_AUTHORIZE_URL}")
    private String googleAuthorUrl;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_TOKEN_URL}")
    private String googleTokenUrl;

    @Value("${GOOGLE_CLIENT_SECRET}")
    private String googleClientSecret;

    private final String scope = "openid email profile";

    @Autowired
    private RestTemplate restTemplate;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String buildAuthorizationUrl(String state) {
        OAuthCodeRequest builder = OAuthCodeRequest.builder()
                .clientId(googleClientId)
                .redirectUri(googleRedirectUri)
                .responseType("code")
                .state(state)
                .scope(scope)
                .build();

        return builder.toUriString(googleAuthorUrl);
    }

    /**
    * 서비스 서버 -> 구글 서버
    */
    public KakoAuthTokenResponse requestAccessToken(String code){
        OAuthTokenRequest oAuthTokenRequest = OAuthTokenRequest.builder()
                .client_id(googleClientId)
                .grant_type("authorization_code")
                .redirect_uri(googleRedirectUri)
                .client_secret(googleClientSecret)
                .code(code)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(oAuthTokenRequest.toMultiValueMap(), headers);

        ResponseEntity<KakoAuthTokenResponse> response = restTemplate.postForEntity(
                googleTokenUrl,
                request,
                KakoAuthTokenResponse.class
        );

        return response.getBody();
    }

    public GoogleUserResponse requestUserInfo(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v2/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUserResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                GoogleUserResponse.class
        );

        return response.getBody();
    }

    public TokenResponse saveOrUpdateUser(GoogleUserResponse userInfo) {
        // 1. 이메일로 기존 사용자 조회
        Optional<User> userOptional = userRepository.findByEmail(userInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            // 2. 기존 사용자가 있으면 필요한 경우 사용자 정보 업데이트
            throw new BusinessException(BusinessError.EMAIL_ALREADY_EXISTS);
        } else {
            // 3. 신규 사용자 등록
            user = User.builder()
                    .email(userInfo.getEmail())
                    .name(userInfo.getName())
                    .build();
            userRepository.save(user);

            // 기본 권한 부여
            Role defaultRole = roleRepository.findById(2)
                    .orElseThrow(() -> new IllegalArgumentException("Default role not found"));
            UserRole userRole = UserRole.builder()
                    .id(new UserRoleId(user.getId(), defaultRole.getId()))
                    .user(user)
                    .role(defaultRole)
                    .build();
            userRoleRepository.save(userRole);
        }

        // 4. JWT 토큰 생성 및 반환
        String jwtToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        return new TokenResponse(jwtToken, refreshToken, user.getEmail());
    }



}
