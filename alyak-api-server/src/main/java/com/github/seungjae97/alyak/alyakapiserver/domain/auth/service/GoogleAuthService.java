package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthTokenRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthCodeRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.KakoAuthTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleAuthService implements OAuthService {


    @Value("${GOOGLE_AUTHORIZE_URL}")
    private String googleAuthorUrl;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;

    @Value("${GOOGLE_TOKEN_URL}")
    private String googleTokenUrl;
    private final String scope = "openid email profile";

    @Autowired
    private RestTemplate restTemplate;
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


}
