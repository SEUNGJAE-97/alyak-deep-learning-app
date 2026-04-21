package com.github.seungjae97.alyak.alyakapiserver.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
@Profile("prod")
@EnableConfigurationProperties(FirebaseProperties.class)
@RequiredArgsConstructor
public class FirebaseConfig {

    private final FirebaseProperties firebaseProperties;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        String path = firebaseProperties.getCredentialsPath();
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("firebase.credentials-path(FIREBASE_CREDENTIALS_PATH)가 비어 있습니다. prod 프로파일에서는 필수입니다.");
        }

        try (InputStream serviceAccount = new FileInputStream(path)) {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount));

            if (firebaseProperties.getProjectId() != null && !firebaseProperties.getProjectId().isBlank()) {
                optionsBuilder.setProjectId(firebaseProperties.getProjectId());
            }

            FirebaseApp app = FirebaseApp.initializeApp(optionsBuilder.build());
            log.info("FirebaseApp 초기화 완료 (source=file:{}, projectId={})", path, app.getOptions().getProjectId());
            return app;
        }
    }
}
