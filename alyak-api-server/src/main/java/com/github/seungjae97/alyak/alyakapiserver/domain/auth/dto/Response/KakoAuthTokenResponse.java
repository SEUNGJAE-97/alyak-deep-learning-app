package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakoAuthTokenResponse {
    private final String token_type;
    private final String access_token;
    private final String expires_in;
    private final String refresh_token;
    private final String refresh_token_expires_in;
    private final String scope;
    private final String id_token;
}
