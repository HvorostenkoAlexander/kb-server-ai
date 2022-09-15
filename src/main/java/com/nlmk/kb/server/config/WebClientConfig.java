package com.nlmk.kb.server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
public class WebClientConfig {

    private static final String CLIENT_REGISTRATION_ID = "keycloak";
    private static final int MAX_IN_MEMORY_SIZE = 2 * 1024 * 1024;

    @Value("${service-web-client.kafka-rest.login}")
    private String kafkaRestLogin;
    @Value("${service-web-client.kafka-rest.password}")
    private String kafkaRestPassword;

    @Bean
    public WebClient basicAuthWebClient() {
        return WebClient.builder()
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    headers.setBasicAuth(kafkaRestLogin, kafkaRestPassword);
                })
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                        .build())
                .build();
    }

    @Bean
    public WebClient defaultWebClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        var oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId(CLIENT_REGISTRATION_ID);

        return WebClient.builder()
                .defaultHeaders(headers -> headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .apply(oauth2Client.oauth2Configuration())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
                        .build())
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        final var provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(provider);

        return authorizedClientManager;
    }

}
