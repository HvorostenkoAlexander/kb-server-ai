package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.nsi.SpAttributeAttestationGroupDto;
import com.nlmk.attestation.product.api.nsi.SpAttributesDto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.Operation;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.service.client.NsiClient;
import com.nlmk.kb.server.service.client.NsiPath;
import com.nlmk.kb.server.service.sender.NsiSender;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class NsiServiceImpl implements NsiSender, NsiClient {

    private static final String OPERATION_RESPONSE_TEMPLATE = "operation [{}], NSI response [{}], request [{}]";

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String nsiUrlDict;


    public NsiServiceImpl(@Value("${service-web-client.nsi-server.url}") String nsiUrlDict,
                          @Value("${service-web-client.timeout:5000}") int timeout,
                          @Qualifier("defaultWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.nsiUrlDict = nsiUrlDict;
    }

    @Override
    public <T> Long sendBodyReturnLong(T body, String targetPath, Operation operation, Long messageId) {
        return exchange(body, Long.class, targetPath, operation, messageId);
    }

    @Override
    public <T> String sendBodyReturnString(T body, String targetPath, Operation operation, Long messageId) {
        return exchange(body, String.class, targetPath, operation, messageId);
    }

    private <T, R> R exchange(T body, Class<R> returned, String targetPath, Operation operation, Long messageId) {
        if (body == null) {
            throw new RemoteServiceSenderException("NsiSender, exchange, пустое тело");
        }

        final R result = webClient.method(operation.getHttpMethod())
                .uri(nsiUrlDict + targetPath)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(returned)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (operation == Operation.D
                            && ex.getRawStatusCode() == HttpStatus.NOT_FOUND.value()) {
                        return Mono.empty();
                    }
                    return Mono.error(ex);
                })
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new RemoteServiceSenderException(String.format(
                                "NsiSender, exchange, ошибка при отправке [%s]", e.getMessage()
                        ))
                ))
                .block();

        log.info(OPERATION_RESPONSE_TEMPLATE, operation, result, body);
        return result;
    }

    @Override
    public Optional<SpAttributeAttestationGroupDto> getAttributeAttestationGroup(String id, String primeId) {

        final var uriBuilder = UriComponentsBuilder.fromHttpUrl(nsiUrlDict)
                .path(NsiPath.GET_ATTRIBUTE_ATTESTATION_GROUP.getValue())
                .path("/" + id);

        return Optional.ofNullable(
                getResponseBody(
                        ParameterizedTypeReference.forType(SpAttributeAttestationGroupDto.class),
                        uriBuilder.encode().build().toUri(),
                        primeId
                )
        );
    }

    @Override
    public Optional<SpAttributesDto> getAttributes(String id, String primeId) {

        final var uriBuilder = UriComponentsBuilder.fromHttpUrl(nsiUrlDict)
                .path(NsiPath.GET_ATTRIBUTES.getValue())
                .path("/" + id);

        return Optional.ofNullable(
                getResponseBody(
                        ParameterizedTypeReference.forType(SpAttributesDto.class),
                        uriBuilder.encode().build().toUri(),
                        primeId
                )
        );
    }

    private <T> T getResponseBody(ParameterizedTypeReference<T> responseType,
                                  URI uri,
                                  String requestId) {
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers -> headers.add(KbConstants.REQUEST_ID_HEADER, requestId))
                .retrieve()
                .bodyToMono(responseType)
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> {
                    log.error("getResponseBody, ошибка получения данных из NSI, [{}]",
                            e.getMessage());
                    return Mono.empty();
                })
                .block();
    }

}
