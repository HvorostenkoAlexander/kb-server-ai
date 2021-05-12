package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.PdmMessage;
import com.nlmk.kb.server.service.NsiClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NsiClientServiceImpl implements NsiClientService {

    private final RestTemplate restTemplate;

    public NsiClientServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message) {

        return null;
    }
}
