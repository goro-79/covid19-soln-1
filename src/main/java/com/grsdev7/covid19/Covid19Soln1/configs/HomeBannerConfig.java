package com.grsdev7.covid19.Covid19Soln1.configs;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "home-banner")
@Data
public class HomeBannerConfig {
    private String text = "Welcome !";
}
