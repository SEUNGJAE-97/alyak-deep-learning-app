package com.github.seungjae97.alyak.alyakapiserver.config;

import com.github.seungjae97.alyak.alyakapiserver.domain.training.service.ModelArchiveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArchiveModelImportInitializer implements ApplicationRunner {

    private final ModelArchiveService modelArchiveService;

    @Value("${archive.import-on-startup:true}")
    private boolean importOnStartup;

    @Value("${archive.runs-root:./archive-runs}")
    private String runsRoot;

    @Override
    public void run(ApplicationArguments args) {
        if (!importOnStartup) {
            return;
        }
        log.info("archive model import started. root={}", runsRoot);
        modelArchiveService.importFromRootPath(runsRoot);
        log.info("archive model import completed. root={}", runsRoot);
    }
}
