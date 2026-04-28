package com.github.seungjae97.alyak.alyakapiserver.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminSessionResponse {
    private Long userId;
    private String email;
    private String name;
    private List<String> roles;
}
