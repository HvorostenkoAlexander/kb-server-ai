package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.SadimMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Date;


public interface SadimMessageService {

    SadimMessage saveMessage(ConsumerRecord consumerRecord);

    PreAttestationParamDto findByAttesstationParam(String pkId,
                                                   String primeId,
                                                   Integer meltNo,
                                                   Integer lotNo);

    Page<SadimMessage> findPreAttestationByParam(String primeId,
                                                 Integer meltNo,
                                                 Integer lotNo,
                                                 Date startDate,
                                                 Date endDate,
                                                 PageRequest of);
}
