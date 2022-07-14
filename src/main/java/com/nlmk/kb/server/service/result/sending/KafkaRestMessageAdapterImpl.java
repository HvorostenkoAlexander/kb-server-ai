package com.nlmk.kb.server.service.result.sending;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.api.MessageValueDto;
import com.nlmk.kb.server.api.MessagesBatchDto;
import com.nlmk.kb.server.entity.KafkaMessageKey;
import com.nlmk.kb.server.exception.KafkaRestException;
import com.nlmk.kb.server.exception.ProductSenderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaRestMessageAdapterImpl implements KafkaRestMessageAdapter {

    private final AvroService avroService;
    private final ObjectMapper objectMapper;

    @Override
    public MessagesBatchDto adapt(SpecificRecordBase specificRecord, KafkaMessageKey messageKey) {
        if (specificRecord == null) {
            log.warn("adapt, сообщение для отправки is null");
            throw new ProductSenderException("Сообщение для отправки is null");
        }

        if (messageKey == null) {
            log.warn("adapt, key для сообщения is null");
            throw new ProductSenderException("Key для сообщения is null");
        }

        try {
            final var jsonString = avroService.toJsonString(specificRecord);
            final var value = objectMapper.readTree(jsonString);
            final var key = messageKey.getKey();

            return new MessagesBatchDto(
                    messageKey.getSchemaKey(),
                    specificRecord.getSchema().toString(),
                    List.of(new MessageValueDto(key, value)));
        } catch (IOException e) {
            log.warn("adapt, ошибка создания MessageValueDto");
            throw new KafkaRestException(e);
        }
    }

}
