package com.communifilm.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Value("${tmdb.base.url}")
    private String tmdbBaseUrl;

    @Bean
    public WebClient tmdbWebClient() {
        return WebClient.builder()
                .baseUrl(tmdbBaseUrl)
                .build();
    }
}
