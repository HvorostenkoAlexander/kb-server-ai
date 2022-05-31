package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.service.PsmSender;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PsmSenderImpl implements PsmSender {

    private final RestTemplate restTemplate;
    private final String psmUrl;

    public PsmSenderImpl(RestTemplate restTemplate,
                         @Value("${psm.url}") String psmUrl) {
        this.restTemplate = restTemplate;
        this.psmUrl = psmUrl;
    }

    @Override
    public Integer postZorder(ZORDERS051 zorder) {

        log.info("post to PSM zorder: {}", zorder);

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));

        final var request = new HttpEntity<>(zorder, headers);

        ResponseEntity<Integer> response = restTemplate
                .exchange(psmUrl + "/sap/order",
                        HttpMethod.POST,
                        request,
                        Integer.class);

        log.info("response from PSM: [{}], request: [{}]", response, request);

        return response.getBody();
    }
}
