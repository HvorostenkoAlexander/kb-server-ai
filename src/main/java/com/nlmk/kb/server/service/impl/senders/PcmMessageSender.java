package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.attestation.product.api.nsi.PcmDto;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmMessageConverter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class PcmMessageSender implements MessageSender {

    private final String url_dictionary;
    private final String type;
    private final PdmMessageConverter pdmMessageConverter;
    private final NsiCommonSender commonSender;

    public PcmMessageSender(PdmMessageConverter pdmMessageConverter,
                            @Value("${nsi.url.pcm}") String url_dictionary,
                            @Value("${kafka.pdm.topic.pcm}") String type,
                            NsiCommonSender commonSender) {

        this.url_dictionary = url_dictionary;
        this.type = type;
        this.pdmMessageConverter = pdmMessageConverter;
        this.commonSender = commonSender;
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message,()-> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = pdmMessageConverter.toPcmDto(message.getDictionary());

        val authHeaderValue = "Authorization: Bearer XYZ";//todo правильно получить authHeaderValue
        HttpHeaders header = new HttpHeaders();
        if (authHeaderValue != null) {
            header.add(HttpHeaders.AUTHORIZATION, authHeaderValue);
        }
        HttpEntity<PcmDto> request = new HttpEntity<>(sendingDto,header);

        return commonSender.exchange(request,url_dictionary, message.getOp());
    }

    @Override
    public String getType() {
        return this.type;
    }
}
