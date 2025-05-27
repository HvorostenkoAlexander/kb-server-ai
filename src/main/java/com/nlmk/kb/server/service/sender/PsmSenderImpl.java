package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.RemoteServiceInternalErrorException;
import com.nlmk.kb.server.exception.RemoteServiceSenderException;
import com.nlmk.kb.server.exception.RemoteServiceTimeoutException;
import com.nlmk.kb.server.util.SenderUtils;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PsmSenderImpl implements PsmSender {

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String psmSapZorder;
    private final String psmSapZmmorder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PsmSenderImpl(@Value("${service-web-client.psm-server.url}") String psmUrl,
                         @Value("${service-web-client.timeout:5000}") int timeout,
                         @Qualifier("defaultWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.psmSapZorder = psmUrl + "/sap/zorder";
        this.psmSapZmmorder = psmUrl + "/sap/zmmorder";
    }

    @Override
    public Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException {
        log.info("postZorder [{}]", zorder);
        final byte[] zorderJson = objectMapper.writeValueAsBytes(zorder);

        final var response = webClient.post()
                .uri(psmSapZorder)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(zorderJson)
                .retrieve()
                .bodyToMono(Integer.class)
                .timeout(Duration.ofMillis(webClientTimeout),
                        Mono.error(new RemoteServiceInternalErrorException("PsmSender, postZorder, server timeout")))
                .onErrorResume(WebClientResponseException.class, e -> {
                            if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                                return Mono.error(
                                        new RemoteServiceInternalErrorException(
                                                String.format("PsmSender, postZorder, internal server error, message [%s]",
                                                        e.getMessage()))
                                );
                            } else {
                                return Mono.error(
                                        new RemoteServiceSenderException(
                                                String.format("PsmSender, postZorder, send error, message [%s]",
                                                        e.getMessage()))
                                );
                            }
                        }
                )
                .block();

        log.info("postZorder, PSM response [{}], JSON length [{}] byte", response,
                objectMapper.writeValueAsBytes(zorder).length);
        return response;
    }

    @Override
    public Integer postZmmorder(ZMMORDERS05DOP zmmorder) throws JsonProcessingException {
        log.info("postZmmorder [{}]", zmmorder);
        final byte[] zmmorderJson = objectMapper.writeValueAsBytes(zmmorder);

        final var response = webClient.post()
                .uri(psmSapZmmorder)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(zmmorderJson)
                .retrieve()
                .bodyToMono(Integer.class)
                .timeout(Duration.ofMillis(webClientTimeout),
                        Mono.error(new RemoteServiceTimeoutException("PsmSender, postZmmorder, server timeout")))
                .onErrorResume(WebClientResponseException.class, e -> {
                            if (e.getRawStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                                return Mono.error(
                                        new RemoteServiceInternalErrorException(
                                                String.format("PsmSender, postZmmorder, internal server error, message [%s]",
                                                        e.getMessage()))
                                );
                            } else {
                                return Mono.error(
                                        new RemoteServiceSenderException(
                                                String.format("PsmSender, postZmmorder, send error, message [%s]",
                                                        e.getMessage()))
                                );
                            }
                        }
                )
                .block();

        log.info("postZmmorder, PSM response [{}], JSON length [{}] byte", response,
                objectMapper.writeValueAsBytes(zmmorder).length);
        return response;
    }
}
