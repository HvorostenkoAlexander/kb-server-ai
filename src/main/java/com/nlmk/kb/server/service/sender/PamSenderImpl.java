package com.nlmk.kb.server.service.sender;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class PamSenderImpl implements PamSender {

    private final String pamUrl;
    private final RestTemplate restTemplate;

    public PamSenderImpl(@Value("${pam.url}") String pamUrl,
                         RestTemplate restTemplate) {
        this.pamUrl = pamUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public ProductAttestationResultDto postAttestationRequest(AttestationRequest request) {
        Assert.notNull(request, "pamAttestationRequest is null");

        log.debug("Отправка AttestationRequest с primeId: [{}]", request.getValue().getData().getPrimeId());

        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));

        ResponseEntity<ProductAttestationResultDto> response = restTemplate.postForEntity(
                pamUrl + "/attestation",
                new HttpEntity<>(request, headers),
                ProductAttestationResultDto.class);
        log.info("PAM-server response: " + response.getBody());
        return response.getBody();
    }

}
