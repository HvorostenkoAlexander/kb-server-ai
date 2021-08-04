package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.service.CcmMessageConverter;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.util.ValueConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmMessageConverterImpl implements CcmMessageConverter {

    private final CommonConverter converter;

    @Override
    public CcmAttestationRequestMessage fromCcmAttestationRequest(AttestationRequest ccmAttestationRequest,
                                                                  String topic,
                                                                  String key,
                                                                  int partition,
                                                                  int offset,
                                                                  String timestamp) {

        val value = ValueConverter.toPamAttestationRequest(ccmAttestationRequest);

        val requestMessage = new CcmAttestationRequestMessage();
        requestMessage.setPartition(partition);
        requestMessage.setOffset(offset);
        requestMessage.setKey(key);
        requestMessage.setTopic(topic);
        requestMessage.setKafkaTs(converter.parseToDate(ccmAttestationRequest.getTs().toString()));
        requestMessage.setKbReceiptTs(
                Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
        );
        requestMessage.setRequest(value);
        if (value.getValue().getData() != null) {
            requestMessage.setPrimeId(value.getValue().getData().getPrimeId());
        }
        return requestMessage;
    }
}
