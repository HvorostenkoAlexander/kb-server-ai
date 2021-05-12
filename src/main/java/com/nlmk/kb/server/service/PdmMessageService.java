package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.PdmMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.Optional;

public interface PdmMessageService {

    /**
     * Сохранение сведений о сообщении из кафка в базу данных kb-server
     * @param record(ConsumerRecord) поступивший из кафки
     * @return Optional of PdmMessage(сохраненный объект в базе данных kb-server)
     */

    public Optional<PdmMessage> save(ConsumerRecord record);
}
