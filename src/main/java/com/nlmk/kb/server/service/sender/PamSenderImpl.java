package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.exception.PamSenderException;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
@Component
public class PamSenderImpl implements PamSender {

    private final WebClient webClient;
    private final int webClientTimeout;
    private final String pamAttestation;

    public PamSenderImpl(@Value("${service-web-client.pam-server.url}") String pamUrl,
                         @Value("${service-web-client.timeout:5000}") int timeout,
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

        final var primeId = SenderUtils.getPrimeId(attestationRequest);
        log.info("postAttestationRequest, primeId [{}]", primeId);

        final var response = webClient.post()
                .uri(pamAttestation)
                .accept(MediaType.APPLICATION_JSON)
                .acceptCharset(StandardCharsets.UTF_8)
                .headers(SenderUtils::addRequestId)
                .bodyValue(attestationRequest)
                .retrieve()
                .bodyToMono(ProductAttestationResultDto.class)
                .timeout(Duration.ofMillis(webClientTimeout))
                .onErrorResume(e -> Mono.error(
                        new PamSenderException(String.format("postAttestationRequest, primeId [%s], send error, message [%s]", primeId, e.getMessage()))
                ))
                .block();

        log.info("postAttestationRequest, primeId [{}], PAM response [{}]", primeId, response);
        return response;
    }

}
