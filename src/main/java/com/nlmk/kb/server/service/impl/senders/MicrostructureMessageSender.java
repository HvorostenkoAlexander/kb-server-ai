package com.nlmk.kb.server.service.impl.senders;

import com.nlmk.kb.server.config.KbConstants;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.DictionaryConfigService;
import com.nlmk.kb.server.service.MessageSender;
import com.nlmk.kb.server.service.NsiCommonSender;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import com.nlmk.kb.server.service.PdmDtoConverter;
import com.nlmk.kb.server.service.PdmMessageCreator;
import com.nlmk.kb.server.util.RestTemplateUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.l3.pdm.SpMicrostructure;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class MicrostructureMessageSender extends BaseSender implements MessageSender, PdmMessageCreator {

    public MicrostructureMessageSender(@Value("${kafka.pdm.topic.microstructure}") String type,
                                       NsiCommonSender commonSender,
                                       PdmDtoConverter pdmDtoConverter,
                                       PdmDictionaryCreator pdmDictionaryCreator,
                                       DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        val sendingDto = super.getPdmDtoConverter().toMicrostructureDto(message.getDictionary());

        val headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        val request = new HttpEntity<>(sendingDto, headers);
        val nsiUrl = getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic());

        return super.getCommonSender().exchange(request, nsiUrl, message.getOp());
    }

    @Override
    public String getType() {
        return super.getType();
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord record) {
        val message = new PdmMessage();
        message.setTopic(record.topic());
        message.setKey((String) record.key());
        message.setOffset(record.offset());
        message.setPartition(record.partition());

        val pdmObject = (SpMicrostructure) record.value();

        val dictionary = super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );
        message.setDictionary(dictionary);
        message.setOp(dictionary.getOp());
        message.setTs(dictionary.getTs());

        return message;
    }
}
