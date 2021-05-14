package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.NsiClientService;
import com.nlmk.kb.server.util.PdmConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class NsiClientServiceImpl implements NsiClientService {

    private final String URL_NSI_DICTIONARY;
    private final RestTemplate restTemplate;
    private final String microstructure="/dict/nsd_microstructure";
    private final String topicMicro;

    public NsiClientServiceImpl(RestTemplateBuilder restTemplateBuilder,
                                @Value("${nsi.url.dict}") String nsiDictionary,
                                @Value("kafka.pdm.topic.microstructure")String topicMicro
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.URL_NSI_DICTIONARY = nsiDictionary;
        this.topicMicro = topicMicro;
    }

    @Override
    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message) {

        String authHeaderValue = "Authorization: Bearer XYZ";

        HttpHeaders header = new HttpHeaders();
        if (authHeaderValue != null) {
            header.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }

        val topic = message.getTopic();

        switch (topic) {
            case "000-1.l3-pdm.cdc.sp-microstructure.0": {
                MicrostructureDto micro = PdmConverter.toMicrostructureDto(message.getDictionary());
                HttpEntity<MicrostructureDto> request = new HttpEntity<>(micro);

                if (message.getOp().equals("I")) {
                    log.info("--- post to NSI: "+request);
                    ResponseEntity<Long> response = restTemplate
                            .exchange(URL_NSI_DICTIONARY + microstructure,
                                    HttpMethod.POST,
                                    request,
                                    Long.class);

                    log.info("--- response from NSI: "+response.getBody());
                    return response;
                }
                if (message.getOp().equals("U")) {
                    log.info("--- put to NSI: "+request);
                    ResponseEntity<Long> response = restTemplate
                            .exchange(URL_NSI_DICTIONARY + microstructure,
                                    HttpMethod.PUT,
                                    request,
                                    Long.class);

                    log.info("--- response from NSI: "+response.getBody());
                    return response;
                }
                if (message.getOp().equals("D")) {
                    log.info("--- delete from NSI: "+request);
                    ResponseEntity<Long> response = restTemplate
                            .exchange(URL_NSI_DICTIONARY + microstructure,
                                    HttpMethod.DELETE,
                                    request,
                                    Long.class);

                    log.info("--- response from NSI: "+response.getBody());
                    return response;
                }
                break;
            }
            default: {
                log.error("Not supported message from topic: {}", topic);
                throw new IllegalArgumentException("Not supported message from topic: " + topic);
            }
        }


//        val pdmMessageDto = PdmConverter.toPdmMessageDto(message);
//
//        ResponseEntity<Long> response = restTemplate.postForEntity(URL_NSI_DICTIONARY,
//                new HttpEntity<>(pdmMessageDto, header),
//                Long.class);
//        log.info("--- response from NSI: "+response.getBody());
        //  return response;
        return null;
    }
}
