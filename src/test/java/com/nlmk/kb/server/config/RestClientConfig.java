package com.nlmk.kb.server.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Deprecated
@TestConfiguration
public class RestClientConfig {


    @Bean("restTemplate")
    public RestTemplate testRestTemplate() {
        return new RestTemplate();
    }
}
