package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.service.PamClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


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
    public void postAttestationRequest(com.nlmk.kb.server.entity.pam.AttestationRequest pamAttestetionRequest) {
       // с целью проверки работы exception handler
       // this.generateException(pamAttestetionRequest.getValue().getOp());

        log.info("--- request: " + pamAttestetionRequest.getValue().getPk());

        String authHeaderValue = "Authorization: Bearer XYZ";//todo правильно получить authHeaderValue

        HttpHeaders header = new HttpHeaders();
        if (authHeaderValue != null) {
            header.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }

        restTemplate.postForEntity(pamUrl,
                new HttpEntity<Object>(pamAttestetionRequest, header),
                com.nlmk.kb.server.entity.pam.AttestationRequest.class);
    }

    private void generateException(String operation){
        if (operation.equals("D")) {
            throw new RuntimeException("не удалось передать сообщение в pam-server.");
        }
    }
}
