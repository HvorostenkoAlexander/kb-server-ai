package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.LengthTkLimitDto;
import com.nlmk.attestation.product.api.nsi.ThicknessTkLimitDto;
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
public class TolLengthMessageSender implements MessageSender {

    private final String url_dictionary;
    private final String type;
    private final PdmMessageConverter pdmMessageConverter;
    private final NsiCommonSender commonSender;

    public TolLengthMessageSender(@Value("${nsi.url.tol-length}")String url_dictionary,
                                 @Value("${kafka.pdm.topic.tol-length}") String topicName,
                                 PdmMessageConverter pdmMessageConverter,
                                 NsiCommonSender commonSender
    ) {
        this.url_dictionary = url_dictionary;
        this.type = topicName;
        this.pdmMessageConverter = pdmMessageConverter;
        this.commonSender = commonSender;
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message,()-> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = pdmMessageConverter.toLengthTkLimitDto(message.getDictionary());

        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        HttpEntity<LengthTkLimitDto> request = new HttpEntity<>(sendingDto,headers);
        ResponseEntity<Long> responseEntity = commonSender.exchange(request,url_dictionary, message.getOp());

        //log.info("response: {}; LengthTkLimitDto: {}",responseEntity.getBody(), sendingDto);

        return responseEntity;
    }

    @Override
    public String getType() {
        return this.type;
    }
}
