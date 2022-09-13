package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.exception.PsmSenderException;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class PsmSenderImpl implements PsmSender {

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String psmSapOrder;
    private final String psmSadim;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PsmSenderImpl(@Value("${service-web-client.psm-server.url}") String psmUrl,
                         @Value("${service-web-client.timeout:2500}") int timeout,
                         @Qualifier("defaultWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.psmSapOrder = psmUrl + "/sap/order";
        this.psmSadim = psmUrl + "/sadim";
    }

    @Override
    public Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException {
        log.info("postZorder [{}]", zorder);
        final byte[] zorderJson = objectMapper.writeValueAsBytes(zorder);

        final var response = webClient.post()
                .uri(psmSapOrder)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(zorderJson)
                .retrieve()
                .bodyToMono(Integer.class)
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new PsmSenderException(String.format("postZorder, send error, message [%s]", e.getMessage()))
                ))
                .block();

        log.info("postZorder, PSM response [{}], JSON length [{}] byte", response, zorderJson.length);
        return response;
    }

    @Override
    public void postSadimMessage(SadimMessageDto sadimMessage) {
        final var primeId = SenderUtils.getPrimeId(sadimMessage);
        log.info("postSadimMessage, primeId [{}], message [{}]", primeId, sadimMessage);

        webClient.post()
                .uri(psmSadim)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(sadimMessage)
                .exchangeToMono(response -> {
                    final var code = response.statusCode();
                    if (code == HttpStatus.CREATED) {
                        log.info("postSadimMessage, primeId [{}], response OK", primeId);
                        return Mono.empty();
                    }
                    return Mono.error(new PsmSenderException(String.format("PSM return code [%d]", code.value())));
                })
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new PsmSenderException(String.format("postSadimMessage, primeId [%s], send error, message [%s]", primeId, e.getMessage()))
                ))
                .block();
    }

}
