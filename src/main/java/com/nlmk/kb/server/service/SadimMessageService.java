package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.SadimMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;


public interface SadimMessageService {

    SadimMessage saveMessage(ConsumerRecord consumerRecord);

    PreAttestationParamDto findByAttesstationParam(String pkId,
                                                   String primeId,
                                                   Integer meltNo,
                                                   Integer lotNo);
}
