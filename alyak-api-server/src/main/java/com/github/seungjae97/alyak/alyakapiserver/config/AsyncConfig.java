package com.github.seungjae97.alyak.alyakapiserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class AsyncConfig {

    @Bean(name = "pillIdentifyTaskScheduler")
    public ThreadPoolTaskScheduler pillIdentifyTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("pill-identify-");
        scheduler.initialize();
        return scheduler;
    }
}

