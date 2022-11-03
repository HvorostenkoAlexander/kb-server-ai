package com.nlmk.kb.server.service.pdm;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class PdmMessageConverterImpl implements PdmMessageConverter {

    private final Map<String, PdmMessageCreator> creators;

    public PdmMessageConverterImpl(List<PdmMessageCreator> allCreators) {
        this.creators = allCreators.stream().collect(
                toMap(PdmMessageCreator::getType, Function.identity())
        );
    }

    @Override
    public PdmMessage fromConsumerRecord(ConsumerRecord<Object, Object> consumerRecord) {

        PdmMessageCreator creator = creators.get(consumerRecord.topic());

        if (creator == null) {
            throw new IllegalArgumentException("Не поддерживается конвертация сообщений для топика: " + consumerRecord.topic());
        }
        return creator.createPdmMessage(consumerRecord);
    }

}
