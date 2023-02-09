package com.nlmk.kb.server.service.pdm.senders;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.entity.pdm.PdmMessage;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import com.nlmk.kb.server.service.pdm.PdmDictionaryCreator;
import com.nlmk.kb.server.service.pdm.PdmDtoConverter;
import com.nlmk.kb.server.service.sender.NsiSender;
import nlmk.l3.pdm.SpTolThickDt;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TolThickDtSender extends BasePdmCreator {

    public TolThickDtSender(@Value("${kafka.pdm.topic.tol-thick-dt}") String type,
                            PdmDtoConverter pdmDtoConverter,
                            NsiSender nsiSender,
                            PdmDictionaryCreator pdmDictionaryCreator,
                            DictionaryConfigService dictionaryConfigService) {
        super(type, pdmDtoConverter, nsiSender, pdmDictionaryCreator, dictionaryConfigService);
    }

    @Override
    Object getBody(PdmMessage message) {
        return super.getPdmDtoConverter().toTolThickDtDto(message.getDictionary());
    }

    @Override
    PdmDictionary getDictionary(ConsumerRecord<Object, Object> consumerRecord) {
        final var pdmObject = (SpTolThickDt) consumerRecord.value();
        return super.getPdmDictionaryCreator().createPdmDictionary(
                pdmObject.getTs(), pdmObject.getOp(), pdmObject.getPk(), pdmObject.getData()
        );
    }

}
