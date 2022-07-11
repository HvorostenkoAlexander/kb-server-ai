package com.nlmk.kb.server.service.result_config;

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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaRestMessageAdapterImpl implements KafkaRestMessageAdapter {

    private final AvroService avroService;

    @Override
    public MessagesBatchDto adapt(SpecificRecordBase record, KafkaMessageKey messageKey) {
        if (record == null) {
            log.warn("Сообщение для отправки is null");
            throw new ProductSenderException("Сообщение для отправки is null");
        }
        if (messageKey == null) {
            log.warn("Key для сообщения is null");
            throw new ProductSenderException("Key для сообщения is null");
        }

        final var schemaValue = record.getSchema().toString();
        final var schemaKey = messageKey.getSchemaKey();
        final var objectMapper = new ObjectMapper();

        List<MessageValueDto> recs = List.of(record)
                .stream()
                .map(t -> {
                    try {
                        final var jsonString = avroService.toJsonString(t);
                        final var node = objectMapper.readTree(jsonString);
                        final var key = messageKey.getKey();

                        return new MessageValueDto(key, node);
                    } catch (IOException ex) {
                        throw new KafkaRestException(ex);
                    }
                })
                .collect(Collectors.toList());

        return new MessagesBatchDto(schemaKey, schemaValue, recs);
    }

}
