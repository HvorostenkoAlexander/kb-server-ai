package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.pdm.PdmOp;
import com.nlmk.kb.server.exception.NsiSenderException;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

    public NsiSenderImpl(@Value("${service-web-client.nsi-server.url}") String nsiUrlDict,
                         @Value("${service-web-client.timeout:2500}") int timeout,
                         @Qualifier("defaultWebClient") WebClient webClient,
                         RestTemplate restTemplate) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.nsiUrlDict = nsiUrlDict;
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> Long exchange(T body, String urlDictionary, PdmOp operation) {
        if (body == null) {
            throw new NsiSenderException("Body is NULL");
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
                        new NsiSenderException(String.format("exchange, send error, message [%s]", e.getMessage()))
                ))
                .block();

        log.info(OPERATION_RESPONSE_TEMPLATE, operation, result, body);
        return result;
    }

    @Override
    public ResponseEntity<Long> exchange(HttpEntity<?> request,
                                         final String urlDictionary,
                                         final PdmOp operation) {
        ResponseEntity<Long> response;

        switch (operation) {
            case I:
                log.info("post to NSI: " + request);
                response = restTemplate
                        .exchange(nsiUrlDict + urlDictionary,
                                HttpMethod.POST,
                                request,
                                Long.class);
                log.info(OPERATION_RESPONSE_TEMPLATE, operation, response, request);
                break;
            case U:
                log.info("put to NSI: " + request);
                response = restTemplate
                        .exchange(nsiUrlDict + urlDictionary,
                                HttpMethod.PUT,
                                request,
                                Long.class);
                log.info(OPERATION_RESPONSE_TEMPLATE, operation, response, request);
                break;
            case D:
                log.info("delete from NSI: " + request);
                try {
                    response = restTemplate
                            .exchange(nsiUrlDict + urlDictionary,
                                    HttpMethod.DELETE,
                                    request,
                                    Long.class);
                    log.info(OPERATION_RESPONSE_TEMPLATE, operation, response, request);
                } catch (HttpClientErrorException hcee) {
                    if (hcee.getRawStatusCode() == HttpStatus.NOT_FOUND.value()) {
                        response = new ResponseEntity<>(0L, HttpStatus.NOT_FOUND);
                        log.warn("response from NSI: [{}], request: [{}]", response, request);

                        return response;
                    }
                    throw hcee;
                }
                break;
            default:
                throw new IllegalArgumentException("not supported operation: " + operation);
        }
        return response;
    }

}
