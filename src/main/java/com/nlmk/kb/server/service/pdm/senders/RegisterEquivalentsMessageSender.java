package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.sender.NsiSender;
import nlmk.l3.pdm.SpRegisterEquivalents;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegisterEquivalentsMessageSender extends BasePdmCreator {

    public RegisterEquivalentsMessageSender(@Value("${kafka.pdm.topic.register-equivalents}") String type,
                                            NsiSender commonSender,
                                            PdmDtoConverter pdmDtoConverter,
                                            PdmDictionaryCreator pdmDictionaryCreator,
                                            DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, commonSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    Object getBody(PdmMessage message) {
        return super.getPdmDtoConverter().toRegisterEquivalentsDto(message.getDictionary());
    }

    @Override
    PdmDictionary getDictionary(ConsumerRecord<Object, Object> consumerRecord) {
        final var pdmObject = (SpRegisterEquivalents) consumerRecord.value();
        return super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );
    }

}
