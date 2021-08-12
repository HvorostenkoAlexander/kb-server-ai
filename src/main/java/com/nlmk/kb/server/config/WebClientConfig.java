package com.nlmk.kb.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "keycloakClient")
    public WebClient keycloakClient(
            @Value("${keycloak.auth-server-url}") String authServerUrl
    ) {

        return WebClient
                .builder()
                .baseUrl(authServerUrl)
                .defaultHeaders(headers -> headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .build();
    }
}
