package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

public interface OAuthService {
    String buildAuthorizationUrl(String state, String redirectUri);

    String buildAuthorizationUrl(String state);
}