package com.nlmk.kb.server.testing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nlmk.sadim.Sadim;
import nlmk.sadim.Strip;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

@Disabled("hand sender")
class SendSadimMessageTest extends SendMessageToKafka {

    private static final String SADIM_TOPIC = "PA-MU.NLMK.P3.HSM";
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void sendSadimMessage() throws JsonProcessingException {
        // nlmk.sadim.Sadim класс составленный на основе примеров сообщений САДиМ, полученных как JSON.
        final var value = new Sadim();
        final var strip = new Strip();
        strip.setTimeRolling(new Date(1_655_880_000_000L));
        strip.setPrimeId("0001020210329001515440422");
        strip.setT12Min(795.0);
        strip.setT12Max(835.0);
        value.setStrips(List.of(strip));
        value.setLotNo(25217); // -> hnum
        value.setMeltNo(2106684); // -> nplv

        ProducerRecord<Object, Object> record = new ProducerRecord<>(
                SADIM_TOPIC,
                randomKey(),
                mapper.writeValueAsString(value)
        );

        sendString(record);
    }

}
