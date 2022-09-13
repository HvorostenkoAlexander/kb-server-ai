package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.exception.PamSenderException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Service
public class PamSenderImpl implements PamSender {

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String pamAttestation;

    public PamSenderImpl(@Value("${service-web-client.pam-server.url}") String pamUrl,
                         @Value("${service-web-client.timeout:2500}") int timeout,
                         @Qualifier("defaultWebClient") WebClient webClient) {
        this.webClient = webClient;
        this.webClientTimeout = timeout;
        this.pamAttestation = pamUrl + "/attestation";
    }

    @Override
    public ProductAttestationResultDto postAttestationRequest(AttestationRequest attestationRequest) {
        if (attestationRequest == null
                || attestationRequest.getValue() == null
                || attestationRequest.getValue().getData() == null) {
            throw new PamSenderException("AttestationRequest is NULL");
        }

        log.info("postAttestationRequest, for primeId [{}]", attestationRequest.getValue().getData().getPrimeId());

        return webClient.post()
                .uri(pamAttestation)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(headers -> headers.add(KbConstants.REQUEST_ID_HEADER, selectRequestId()))
                .bodyValue(attestationRequest)
                .retrieve()
                .bodyToMono(ProductAttestationResultDto.class)
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new PamSenderException(String.format("postAttestationRequest, send error, message [%s]", e.getMessage()))
                ))
                .block();
    }

    private String selectRequestId() {
        final var requestIdKafka = MDC.get(KbConstants.KAFKA_ID);
        final var requestIdRest = MDC.get(KbConstants.REQUEST_ID_KEY);
        return (requestIdKafka != null) ? requestIdKafka : requestIdRest;
    }

}
