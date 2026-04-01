package com.github.seungjae97.alyak.alyakapiserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    /**
     * 서비스 계정 JSON 파일의 절대/상대 경로 (예: /secrets/firebase-adminsdk.json)
     */
    private String credentialsPath = "";

    /**
     * Firebase 프로젝트 ID (예: uplifted-studio-457001-h1)
     */
    private String projectId = "";
}
