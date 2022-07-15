package com.nlmk.kb.server.service.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.exception.PsmSenderException;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PsmSenderImpl implements PsmSender {

    private final RestTemplate restTemplate;
    private final String psmUrl;
    private final ObjectMapper objectMapper;

    public PsmSenderImpl(RestTemplate restTemplate,
                         ObjectMapper objectMapper,
                         @Value("${service-web-client.psm-server.url}") String psmUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.psmUrl = psmUrl;
    }

    @Override
    public Integer postZorder(ZORDERS051 zorder) throws JsonProcessingException {
        log.info("post to PSM zorder: {}", zorder);

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        headers.setContentType(MediaType.APPLICATION_JSON);

        final String zorderJson = objectMapper.writeValueAsString(zorder);

        final var request = new HttpEntity<>(zorderJson, headers);

        ResponseEntity<Integer> response = restTemplate.exchange(
                psmUrl + "/sap/order",
                HttpMethod.POST,
                request,
                Integer.class);

        log.info("response from PSM: [{}], request: [{}]", response, request);
        return response.getBody();
    }

    @Override
    public void postSadimMessage(SadimMessageDto dto) {
        final var primeId = getPrimeId(dto);
        log.info("postSadimMessage, for primeId [{}], message [{}]", primeId, dto);
        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    psmUrl + "/sadim",
                    HttpMethod.POST,
                    new HttpEntity<>(dto, headers),
                    Object.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("postSadimMessage, for primeId [{}] is OK", primeId);
                return;
            }

            throw new PsmSenderException(String.format("postSadimMessage, for primeId [%s], PSM return code [%d]", primeId, response.getStatusCode().value()));
        } catch (Exception e) {
            throw new PsmSenderException(String.format("postSadimMessage, for primeId [%s], error [%s]", primeId, e.getMessage()));
        }
    }

    private String getPrimeId(SadimMessageDto dto) {
        if (dto == null || dto.getParam() == null) {
            return null;
        }
        return dto.getParam().getPrimeId();
    }

}
