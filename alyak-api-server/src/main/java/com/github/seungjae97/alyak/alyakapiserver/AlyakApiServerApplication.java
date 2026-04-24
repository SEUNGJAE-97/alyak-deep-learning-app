package com.github.seungjae97.alyak.alyakapiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
@EnableScheduling
public class AlyakApiServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlyakApiServerApplication.class, args);
    }

}
