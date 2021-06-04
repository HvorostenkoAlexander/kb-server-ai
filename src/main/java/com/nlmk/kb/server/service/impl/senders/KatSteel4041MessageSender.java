package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.attestation.product.api.nsi.SteelCategoryG4041Dto;
import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import com.nlmk.kb.server.service.PdmDtoConverter;
import com.nlmk.kb.server.service.PdmMessageCreator;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.SpKatSteelMarkGost4041;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class KatSteel4041MessageSender implements MessageSender, PdmMessageCreator {

    private final String url_dictionary;
    private final String type;
    private final PdmDtoConverter pdmDtoConverter;
    private final NsiCommonSender commonSender;
    private final PdmDictionaryCreator pdmDictionaryCreator;

    public KatSteel4041MessageSender(@Value("${nsi.url.kat-steel-4041}") String url_dictionary,
                                     @Value("${kafka.pdm.topic.kat-steel-4041}") String topicName,
                                     PdmDtoConverter pdmDtoConverter,
                                     NsiCommonSender commonSender,
                                     PdmDictionaryCreator pdmDictionaryCreator) {
        this.url_dictionary = url_dictionary;
        this.type = topicName;
        this.pdmDtoConverter = pdmDtoConverter;
        this.commonSender = commonSender;
        this.pdmDictionaryCreator = pdmDictionaryCreator;
    }
    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message,()-> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = pdmDtoConverter.toKatSteel4041Dto(message.getDictionary());

        HttpHeaders headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        HttpEntity<SteelCategoryG4041Dto> request = new HttpEntity<>(sendingDto,headers);

        return commonSender.exchange(request,url_dictionary, message.getOp());
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord record) {
        PdmMessage message = new PdmMessage();
        message.setTopic(record.topic());
        message.setKey((String) record.key());
        message.setOffset(record.offset());
        message.setPartition(record.partition());

        SpKatSteelMarkGost4041 pdmObject = (SpKatSteelMarkGost4041) record.value();

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                pdmObject.getTs(),pdmObject.getOp(),pdmObject.getPk(),pdmObject.getData()
        );
        message.setDictionary(dictionary);
        message.setOp(dictionary.getOp());
        message.setTs(dictionary.getTs());

        return message;
    }
}
