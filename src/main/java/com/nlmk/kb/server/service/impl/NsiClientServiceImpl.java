package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.NsiClientService;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NsiClientServiceImpl implements NsiClientService {

    private final String URL_NSI_DICTIONARY;
    private final RestTemplate restTemplate;

    public NsiClientServiceImpl(RestTemplateBuilder restTemplateBuilder,
                                @Value("${nsi.url.dict}")String nsiDictionary) {
        this.restTemplate = restTemplateBuilder.build();
        this.URL_NSI_DICTIONARY = nsiDictionary;
    }

    @Override
    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message) {

        String authHeaderValue = "Authorization: Bearer XYZ";

        HttpHeaders header = new HttpHeaders();
        if (authHeaderValue != null) {
            header.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }

        val pdmMessageDto = PdmConverter.toPdmMessageDto(message);

        ResponseEntity<Long> response = restTemplate.postForEntity(URL_NSI_DICTIONARY,
                new HttpEntity<>(pdmMessageDto, header),
                Long.class);
        log.info("--- response from NSI: "+response.getBody());
        return response;
    }
}
