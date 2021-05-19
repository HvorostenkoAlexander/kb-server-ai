package com.nlmk.kb.server.service.impl;

import com.nlmk.attestation.product.api.nsi.ThicknessTkLimitDto;
import com.nlmk.attestation.product.api.nsi.WidthTkLimitDto;
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
public class TolWidthMessageSender implements MessageSender {

    private final String url_dictionary;
    private final String type;
    private final PdmMessageConverter pdmMessageConverter;
    private final NsiCommonSender commonSender;

    public TolWidthMessageSender(@Value("${nsi.url.tol-width}")String url_dictionary,
                                 @Value("${kafka.pdm.topic.tol-width}") String topicName,
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

        val sendingDto = pdmMessageConverter.toWidthTkLimitDto(message.getDictionary());

        val authHeaderValue = "Authorization: Bearer XYZ";//todo правильно получить authHeaderValue
        HttpHeaders headers = RestTemplateUtils.prepareHeaders(authHeaderValue, MDC.get(KbConstants.KAFKA_ID));
        if (authHeaderValue != null) {
            headers.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }
        HttpEntity<WidthTkLimitDto> request = new HttpEntity<>(sendingDto,headers);

        return commonSender.exchange(request,url_dictionary, message.getOp());
    }

    @Override
    public String getType() {
        return this.type;
    }
}
