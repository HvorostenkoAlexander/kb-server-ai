package com.nlmk.kb.server.config;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientConfig {

    @Value("${service-web-client.kafka-rest.login}")
    private String kafkaRestLogin;
    @Value("${service-web-client.kafka-rest.password}")
    private String kafkaRestPassword;

    @Bean
    public WebClient defaultWebClient() {
        return WebClient.builder().build();
    }

    @Bean
    public WebClient basicAuthWebClient() {
        return WebClient.builder()
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.setBasicAuth(kafkaRestLogin, kafkaRestPassword);
                })
                .build();
    }

}
