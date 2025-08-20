package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@Getter
@Builder
@RequiredArgsConstructor
public class KakaoAuthCodeRequest {
    private final String clientId;
    private final String redirectUri;
    private final String responseType;
    private final String scope;
    private final String state;
    private final String prompt;
    private final String loginHint;
    private final String nonce;

    public String toUriString(String baseAuthorizeUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseAuthorizeUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", responseType);

        if (scope != null) builder.queryParam("scope", scope);
        if (state != null) builder.queryParam("state", state);
        if (prompt != null) builder.queryParam("prompt", prompt);
        if (loginHint != null) builder.queryParam("login_hint", loginHint);
        if (nonce != null) builder.queryParam("nonce", nonce);

        return builder.toUriString();
    }
}
