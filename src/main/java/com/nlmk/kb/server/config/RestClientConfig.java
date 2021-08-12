package com.nlmk.kb.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    private static final String TOKEN_URL_TEMPLATE = "%s/realms/%s/protocol/openid-connect/token";

    /**
     * RestTemplate, который авторизуется в keycloak и делает запрос с соотвествующим токеном.
     *
     * @param serverUrl    адрес сервера keycloak
     * @param realm        реалм
     * @param clientId     клиент
     * @param clientSecret секрет клиента
     * @return сконфигурированный rest template
     */
    @Bean("restTemplate")
    public RestTemplate oathRestTemplate(@Value("${keycloak.auth-server-url}") String serverUrl,
                                         @Value("${keycloak.realm}") String realm,
                                         @Value("${kb.oauth2.client.id}") String clientId,
                                         @Value("${kb.oauth2.client.secret}") String clientSecret,
                                         ClientHttpRequestFactory clientHttpRequestFactory) {
        String tokenUrl = String.format(TOKEN_URL_TEMPLATE, serverUrl, realm);

        ClientCredentialsResourceDetails clientCredentialsResourceDetails = new ClientCredentialsResourceDetails();
        clientCredentialsResourceDetails.setAccessTokenUri(tokenUrl);
        clientCredentialsResourceDetails.setClientId(clientId);
        clientCredentialsResourceDetails.setClientSecret(clientSecret);

        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(clientCredentialsResourceDetails);
        oAuth2RestTemplate.setRequestFactory(clientHttpRequestFactory);
        return oAuth2RestTemplate;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000); //timeout in milliseconds
        requestFactory.setReadTimeout(10000); //timeout in milliseconds
        return requestFactory;
    }
}
