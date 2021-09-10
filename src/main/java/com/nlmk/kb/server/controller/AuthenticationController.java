package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.exception.KeycloakAuthorizationException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Collections;

@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
@Hidden
public class AuthenticationController {

    @Value("${keycloak.realm}")
    String realm;
    private static final String TOKEN_URL_TEMPLATE = "/realms/%s/protocol/openid-connect/token";
    private final WebClient keycloakClient;

    @Getter
    @Setter
    public static class KeycloakResponse {
        String token_type;
        String access_token;
        Integer expires_in;
        String refresh_token;
        Integer refresh_expires_in;
    }

    @PostMapping("/token")
    public ResponseEntity getToken(@RequestHeader("Authorization") String authHeader) {

        String[] authData =
                new String(Base64.getDecoder().decode(authHeader.replace("Basic ", ""))).split(":");

        String tokenUrl = String.format(TOKEN_URL_TEMPLATE, realm);

        MultiValueMap<String, String> credentials = new LinkedMultiValueMap<>();
        credentials.put("grant_type", Collections.singletonList("client_credentials"));
        credentials.put("client_id", Collections.singletonList(authData[0]));
        credentials.put("client_secret", Collections.singletonList(authData[1]));

        try {
            KeycloakResponse res = keycloakClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromFormData(credentials))
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, response -> response
                            .bodyToMono(String.class)
                            .flatMap(error -> Mono.error(new KeycloakAuthorizationException(error))))
                    .bodyToMono(KeycloakResponse.class)
                    .block();

            return ResponseEntity.ok().body(res);
        } catch (KeycloakAuthorizationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
