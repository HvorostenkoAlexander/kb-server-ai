package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class MicrostructureMessageSender implements MessageSender {

    private final RestTemplate restTemplate;
    private final String url_dictionary;
    private final String URL_NSI_DICTIONARY;

    public MicrostructureMessageSender(RestTemplateBuilder restTemplateBuilder,
                                       @Value("${nsi.url.microstructure}")String url_dictionary,
                                       @Value("${nsi.url.dict}") String nsiDictionary
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url_dictionary = url_dictionary;
        this.URL_NSI_DICTIONARY = nsiDictionary;
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message,()-> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = PdmConverter.toMicrostructureDto(message.getDictionary());

        val authHeaderValue = "Authorization: Bearer XYZ";//todo правильно получить authHeaderValue

        HttpHeaders header = new HttpHeaders();
        if (authHeaderValue != null) {
            header.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }
        HttpEntity<MicrostructureDto> request = new HttpEntity<>(sendingDto,header);
        ResponseEntity<Long> response=new ResponseEntity<>(0L,HttpStatus.BAD_REQUEST);

        val operation = message.getOp();

        switch (operation) {
            case "I":{
                log.info("--- post to NSI: "+request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.POST,
                                request,
                                Long.class);
                break;
            }
            case "U":{
                log.info("--- put to NSI: "+request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.PUT,
                                request,
                                Long.class);
                break;
            }
            case "D" :{
                log.info("--- delete from NSI: "+request);
                response = restTemplate
                        .exchange(URL_NSI_DICTIONARY + url_dictionary,
                                HttpMethod.DELETE,
                                request,
                                Long.class);
                break;
            }
            default:{
                throw new IllegalArgumentException("not supported operation: "+operation);
            }
        }
        log.info("--- response from NSI: "+response.getBody());
        return response;
    }
}
