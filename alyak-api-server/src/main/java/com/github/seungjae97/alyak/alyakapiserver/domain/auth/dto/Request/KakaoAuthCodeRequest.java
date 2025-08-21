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
    private final String state;

    public String toUriString(String baseAuthorizeUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseAuthorizeUrl)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", responseType);

        if (state != null) builder.queryParam("state", state);

        return builder.toUriString();
    }
}
