package com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request;

public class KakaoLoginRequest {

    private String client_id;
    private String redirect_uri;
    private final String response_type = "code";
}
