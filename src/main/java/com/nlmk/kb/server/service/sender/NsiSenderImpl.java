package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class NsiSenderImpl implements NsiSender {

    private static final String OPERATION_RESPONSE_TEMPLATE = "operation [{}], NSI response [{}], request [{}]";

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String nsiUrlDict;

    public NsiSenderImpl(@Value("${service-web-client.nsi-server.url}") String nsiUrlDict,
                         @Value("${service-web-client.timeout:5000}") int timeout,
                         @Qualifier("defaultWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.nsiUrlDict = nsiUrlDict;
    }

    @Override
    public <T> ResponseEntity<Long> exchange(T body, String urlDictionary, PdmOp operation) {
        if (body == null) {
            throw new RemoteServiceSenderException("NsiSender, Body is NULL");
        }

        final var result = webClient.method(operation.getHttpMethod())
                .uri(nsiUrlDict + urlDictionary)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    if (operation == PdmOp.D
                            && ex.getRawStatusCode() == HttpStatus.NOT_FOUND.value()) {
                        return Mono.just(0L);
                    }
                    return Mono.error(ex);
                })
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new RemoteServiceSenderException(String.format("NsiSender, exchange, send error, message [%s]", e.getMessage()))
                ))
                .block();

        log.info(OPERATION_RESPONSE_TEMPLATE, operation, result, body);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
