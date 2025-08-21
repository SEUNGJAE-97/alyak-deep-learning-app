package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.OAuthCodeRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleAuthService implements OAuthService {


    @Value("${GOOGLE_AUTHORIZE_URL}")
    private String googleAuthorUrl;

    @Value("${GOOGLE_REDIRECT_URI}")
    private String googleRedirectUri;

    @Value("${GOOGLE_CLIENT_ID}")
    private String googleClientId;
    private final String scope = "openid email profile";

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
}
