package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class MdmMessageConverterImpl implements MdmMessageConverter {

    private final Map<String, MdmMessageCreator> creators;

    public MdmMessageConverterImpl(List<MdmMessageCreator> allCreators) {
        this.creators = allCreators.stream().collect(
                toMap(MdmMessageCreator::getType, Function.identity())
        );
    }

    @Override
    public MdmMessage fromConsumerRecord(ConsumerRecord<Object, Object> consumerRecord) {

        MdmMessageCreator creator = creators.get(consumerRecord.topic());

        if (creator == null) {
            throw new IllegalArgumentException("Не поддерживается конвертация сообщений для топика: " + consumerRecord.topic());
        }

        return creator.createMdmMessage(consumerRecord);
    }
}
