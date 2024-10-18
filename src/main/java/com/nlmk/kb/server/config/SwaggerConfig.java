package com.nlmk.kb.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    private final String authServerUrl;
    private final String realm;

    public SwaggerConfig(@Value("${keycloak.auth-server-url}")
                         String authServerUrl,
                         @Value("${keycloak.realm}")
                         String realm) {
        this.authServerUrl = authServerUrl;
        this.realm = realm;
    }

    @Bean
    public GroupedOpenApi attestationRequestsAPI() {
        return GroupedOpenApi.builder()
                .group("Принятие запросов на аттестацию")
                .packagesToScan("com.nlmk.kb.server.controller")
                .pathsToMatch(
                        "/send_attestation_result",
                        "/sap_message/**",
                        "/pdm_message/**",
                        "/launch_attestation/**",
                        "/attestation/**",
                        "/ccm_source_message",
                        "/attestation_request",
                        "/attestation/request/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi pdmConfigAPI() {
        return GroupedOpenApi.builder()
                .group("Конфигурация справочников PDM")
                .packagesToScan("com.nlmk.kb.server.controller")
                .pathsToMatch(
                        "/configuration",
                        "/configuration/*"
                )
                .pathsToExclude(
                        "/configuration/avro",
                        "/configuration/topics/**"
                )
                .build();
    }

    @Bean
    public GroupedOpenApi topicsConfigAPI() {
        return GroupedOpenApi.builder()
                .group("Конфигурация топиков")
                .packagesToScan("com.nlmk.kb.server.controller")
                .pathsToMatch(
                        "/configuration/topics/**",
                        "/configuration/avro"
                )
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        final var realms = "/realms/";
        final var auth = "/protocol/openid-connect/auth";
        final var token = "/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new Info()
                        .title("Kafka Broker (KB) Service")
                        .version("1.0.0")
                        .description("Модуль интеграции с внешними системами"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Oauth2 flow")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authServerUrl + realms + realm + auth)
                                                .tokenUrl(authServerUrl + realms + realm + token)
                                                .scopes(new Scopes())
                                        ))
                        ))
                .security(Collections.singletonList(
                        new SecurityRequirement().addList("bearer-key")
                ));
    }
}
