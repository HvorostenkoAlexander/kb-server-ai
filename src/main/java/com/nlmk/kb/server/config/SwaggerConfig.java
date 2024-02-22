package com.nlmk.kb.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
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
