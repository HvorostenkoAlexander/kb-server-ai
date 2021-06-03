package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.MicrostructureDto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmMessageConverter;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class MicrostructureMessageSender implements MessageSender {

    private final String url_dictionary;
    private final String type;
    private final PdmMessageConverter pdmMessageConverter;
    private final NsiCommonSender commonSender;

    public MicrostructureMessageSender(@Value("${nsi.url.microstructure}")String url_dictionary,
                                       @Value("${kafka.pdm.topic.microstructure}") String topicName,
                                       NsiCommonSender commonSender,
                                       PdmMessageConverter pdmMessageConverter
    ) {
        this.url_dictionary = url_dictionary;
        this.type = topicName;
        this.commonSender = commonSender;
        this.pdmMessageConverter = pdmMessageConverter;
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message,()-> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = pdmMessageConverter.toMicrostructureDto(message.getDictionary());

        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        HttpEntity<MicrostructureDto> request = new HttpEntity<>(sendingDto, headers);

        return commonSender.exchange(request,url_dictionary, message.getOp());
    }

    @Override
    public String getType() {
        return this.type;
    }
}
