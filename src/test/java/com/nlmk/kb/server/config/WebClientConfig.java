package com.nlmk.kb.server.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientConfig {

    @Bean
    public WebClient defaultWebClient() {
        return WebClient.builder().build();
    }

}
