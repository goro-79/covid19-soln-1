package com.grsdev7.covid19.Covid19Soln1;

import com.grsdev7.covid19.Covid19Soln1.configs.HomeBannerConfig;
import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import com.grsdev7.covid19.Covid19Soln1.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableConfigurationProperties(value = {HomeBannerConfig.class})
@RequiredArgsConstructor
@EnableMongoAuditing
public class Covid19Soln1Application {

    private final RequestRepository requestRepository;

    public static void main(String[] args) {
        SpringApplication.run(Covid19Soln1Application.class, args);
    }
}
