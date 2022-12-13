package com.nlmk.kb.server.service.sender;

import com.nlmk.kb.server.entity.Operation;
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
    public <T> ResponseEntity<Long> sendBodyReturnLong(T body, String targetPath, Operation operation) {
        return exchange(body, Long.class, targetPath, operation);
    }

    @Override
    public <T> ResponseEntity<String> sendBodyReturnString(T body, String targetPath, Operation operation) {
        return exchange(body, String.class, targetPath, operation);
    }

    private <T, R> ResponseEntity<R> exchange(T body,
                                              Class<R> returned,
                                              String targetPath,
                                              Operation operation) {
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
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
