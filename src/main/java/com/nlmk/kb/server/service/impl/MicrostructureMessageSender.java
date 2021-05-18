package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final String type;

    @Autowired
    private NsiCommonSender nsiCommonSender; //todo проверить как работает, если OK то final

    public MicrostructureMessageSender(RestTemplateBuilder restTemplateBuilder,
                                       @Value("${nsi.url.microstructure}")String url_dictionary,
                                       @Value("${nsi.url.dict}") String nsiDictionary,
                                       @Value("${kafka.pdm.topic.microstructure}") String topicName
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url_dictionary = url_dictionary;
        this.URL_NSI_DICTIONARY = nsiDictionary;
        this.type = topicName;
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

        return nsiCommonSender.exchange(request,url_dictionary, message.getOp());
    }

    @Override
    public String getType() {
        return this.type;
    }
}
