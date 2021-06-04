package com.nlmk.kb.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Value("${keycloak.auth-server-url}") String keycloakServerUrl;
    @Value("${keycloak.realm}") String keycloakRealm;

    @Bean
    public OpenAPI customOpenAPI() {

        var authUrl = String.format("%s/realms/%s/protocol/openid-connect", keycloakServerUrl, keycloakRealm);

        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-key", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .description("Oauth2 flow")
                    .flows(new OAuthFlows()
                        .clientCredentials(new OAuthFlow()
                            .authorizationUrl(authUrl + "/auth")
                            .refreshUrl(authUrl + "/token")
                            .tokenUrl(authUrl + "/token")
                            .scopes(new Scopes())
                        ))
                ))
            .security(Collections.singletonList(
                new SecurityRequirement().addList("bearer-key")
            ));
    }
}
