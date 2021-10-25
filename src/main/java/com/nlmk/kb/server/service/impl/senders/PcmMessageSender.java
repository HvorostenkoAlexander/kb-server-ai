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
import nlmk.l3.pdm.SpPcm;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
public class PcmMessageSender extends BaseSender implements MessageSender, PdmMessageCreator {

    public PcmMessageSender(PdmDtoConverter pdmDtoConverter,
                            @Value("${kafka.pdm.topic.pcm}") String type,
                            NsiCommonSender commonSender,
                            PdmDictionaryCreator pdmDictionaryCreator,
                            DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    public ResponseEntity<Long> send(PdmMessage message) {
        Assert.notNull(message, () -> {
            throw new IllegalArgumentException("message for sending is NULL");
        });

        final var sendingDto = super.getPdmDtoConverter().toPcmDto(message.getDictionary());

        final var headers = RestTemplateUtils.prepareHeaders(MDC.get(KbConstants.KAFKA_ID));
        final var request = new HttpEntity<>(sendingDto, headers);
        final var nsiUrl = getDictionaryConfigService().getDictionaryUrlByTopic(message.getTopic());

        return super.getCommonSender().exchange(request, nsiUrl, message.getOp());
    }

    @Override
    public PdmMessage createPdmMessage(ConsumerRecord record) {
        SpPcm pdmObject = (SpPcm) record.value();

        final var dictionary = super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );

        return PdmMessage.builder()
                .topic(record.topic())
                .key((String) record.key())
                .offset(record.offset())
                .partition(record.partition())
                .dictionary(dictionary)
                .op(dictionary.getOp())
                .ts(dictionary.getTs())
                .build();
    }
}
