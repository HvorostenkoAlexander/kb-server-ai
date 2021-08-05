package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.service.PamClientService;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.nlmk.kb.server.entity.pam.AttestationRequest;


@Slf4j
@Service
public class PamClientServiceImpl implements PamClientService {

    private final String pamUrl;
    private final RestTemplate restTemplate;

    public PamClientServiceImpl( @Value("${pam.url}") String pamUrl,
                                 RestTemplate restTemplate) {
        this.pamUrl = pamUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public Long postAttestationRequest(AttestationRequest pamAttestationRequest) {

        log.debug("request: " + pamAttestationRequest.getValue().getPk());

        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));

        ResponseEntity<Long> response = restTemplate.postForEntity(pamUrl,
                new HttpEntity<>(pamAttestationRequest, headers),
                Long.class);
        log.info("PAM-server response: "+response.getBody());
        return response.getBody();
    }
}
