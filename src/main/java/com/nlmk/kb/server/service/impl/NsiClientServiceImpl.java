package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.NsiClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class NsiClientServiceImpl implements NsiClientService {

    private final MicrostructureMessageSender microstructureMessageSender;

    @Override
    public ResponseEntity<Long> sendPdmDictionary(PdmMessage message) {

        val topic = message.getTopic();
        ResponseEntity<Long> response=new ResponseEntity<>(0L, HttpStatus.BAD_REQUEST);

        switch (topic) {
            case "000-1.l3-pdm.cdc.sp-microstructure.0": {
                response = microstructureMessageSender.send(message);
                break;
            }
            case"kafka.pdm.topic.asap-chemical-properties":{
                //response =
            }
            default: {
                log.error("Not supported message from topic: {}", topic);
                throw new IllegalArgumentException("Not supported message from topic: " + topic);
            }
        }
        return response;
    }
}
