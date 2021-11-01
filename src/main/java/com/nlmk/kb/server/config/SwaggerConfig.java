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

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Oauth2 flow")
                                .flows(new OAuthFlows()
                                        .clientCredentials(new OAuthFlow()
                                                .authorizationUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/auth")
                                                .refreshUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                                                .tokenUrl(authServerUrl + "/realms/" + realm + "/protocol/openid-connect/token")
                                                .scopes(new Scopes())
                                        ))
                        ))
                .security(Collections.singletonList(
                        new SecurityRequirement().addList("bearer-key")
                ));
    }
}
