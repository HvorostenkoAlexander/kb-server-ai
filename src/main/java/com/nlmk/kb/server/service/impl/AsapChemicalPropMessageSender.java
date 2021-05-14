package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AsapChemicalPropMessageSender implements MessageSender {

    private final RestTemplate restTemplate;
    private final String url_dictionary;
    private final String URL_NSI_DICTIONARY;

    public AsapChemicalPropMessageSender(RestTemplateBuilder restTemplateBuilder,
                                       @Value("")String url_dictionary,
                                       @Value("${nsi.url.dict}") String nsiDictionary
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url_dictionary = url_dictionary;
        this.URL_NSI_DICTIONARY = nsiDictionary;
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        return null;
    }
}
