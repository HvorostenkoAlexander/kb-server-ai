package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.NsiClientService;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class NsiClientServiceImpl implements NsiClientService {

    private final MicrostructureMessageSender microstructureMessageSender;
    private final AsapChemicalPropMessageSender chemicalPropMessageSender;
    private final String topicMicro;
    private final String topicChemicalProp;

    public NsiClientServiceImpl(MicrostructureMessageSender microstructureMessageSender,
                                AsapChemicalPropMessageSender chemicalPropMessageSender,
                                @Value("${kafka.pdm.topic.microstructure}") String topicMicro,
                                @Value("${kafka.pdm.topic.asap-chemical-properties}") String topicChemicalProp
    ) {
        this.microstructureMessageSender = microstructureMessageSender;
        this.chemicalPropMessageSender = chemicalPropMessageSender;
        this.topicMicro = topicMicro;
        this.topicChemicalProp = topicChemicalProp;
    }

    @Override
    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message) {

        val topic = message.getTopic();
        ResponseEntity<Long> response=new ResponseEntity<>(0L, HttpStatus.BAD_REQUEST);
        log.info("--- TOPIC: {}",topic);
        if (topic.equals(topicMicro)){
            response = microstructureMessageSender.send(message);
        } else if (topic.equals(topicChemicalProp)) {
            response = chemicalPropMessageSender.send(message);
        } else {
            log.error("Not supported message from topic: {}", topic);
            throw new IllegalArgumentException("Not supported message from topic: " + topic);
        }

        return response;
    }
}
