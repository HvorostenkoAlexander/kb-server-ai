package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class PamSenderImpl implements PamSender {

    private final String pamAttestation;
    private final RestTemplate restTemplate;

    public PamSenderImpl(@Value("${service-web-client.pam-server.url}") String pamUrl,
                         RestTemplate restTemplate) {
        this.pamAttestation = pamUrl + "/attestation";
        this.restTemplate = restTemplate;
    }

    @Override
    public ProductAttestationResultDto postAttestationRequest(AttestationRequest request) {
        Assert.notNull(request, "pamAttestationRequest is null");

        log.info("postAttestationRequest, primeId: [{}]", request.getValue().getData().getPrimeId());

        final var requestIdKafka = MDC.get(KbConstants.KAFKA_ID);
        final var requestIdRest = MDC.get(KbConstants.REQUEST_ID_KEY);
        final var requestId = (requestIdKafka != null) ? requestIdKafka : requestIdRest;

        ResponseEntity<ProductAttestationResultDto> response = restTemplate.postForEntity(
                pamAttestation,
                new HttpEntity<>(request, RestTemplateUtils.prepareHeaders(requestId)),
                ProductAttestationResultDto.class);
        log.info("postAttestationRequest, PAM-server response: " + response.getBody());
        return response.getBody();
    }

}
