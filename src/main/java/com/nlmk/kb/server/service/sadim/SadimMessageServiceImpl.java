package com.nlmk.kb.server.service.sadim;

import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.service.PsmSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class SadimMessageServiceImpl implements SadimMessageService {

    private final SadimJsonParser sadimJsonParser;
    private final PsmSender psmSender;

    @Override
    public String saveMessage(ConsumerRecord<Object, Object> consumerRecord) {
        Assert.notNull(consumerRecord, "consumerRecord must not be null");
        Assert.notNull(consumerRecord.value(), "consumerRecord.value must not be null");

        // получение параметров
        final var paramDto = sadimJsonParser.getParam(consumerRecord.value().toString())
                .orElseThrow(() -> new SadimJsonProcessingException("Ошибка получения параметров из сообщения SADIM."));
        log.debug("SADIM message with offset [{}] paramDto: [{}]", consumerRecord.offset(), paramDto);

        // подготовка сообщения
        final var dto = SadimMessageDto.builder()
                .key(consumerRecord.key().toString())
                .partition(consumerRecord.partition())
                .offset(consumerRecord.offset())
                .param(paramDto)
                .build();
        // отправка по сети
        psmSender.postSadimMessage(dto);

        return getPrimeId(paramDto);
    }

    private String getPrimeId(SadimMessageDto.ParamDto param) {
        if (param == null) {
            return null;
        }
        return param.getPrimeId();
    }

}
