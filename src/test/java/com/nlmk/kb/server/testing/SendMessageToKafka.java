package com.nlmk.kb.server.testing;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Assertions;

import java.time.Instant;
import java.util.Properties;

public class SendMessageToKafka {

    private final KafkaProducer<Object, Object> stringProducer;
    private final KafkaProducer<Object, Object> avroProducer;

    public SendMessageToKafka() {
        final var avroProps = new Properties();
        avroProps.put("bootstrap.servers", "localhost:29092");
        avroProps.put("schema.registry.url", "http://localhost:28881");
        avroProps.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProducer = new KafkaProducer<>(avroProps);

        final var stringProps = new Properties();
        stringProps.put("bootstrap.servers", "localhost:29092");
        stringProps.put("schema.registry.url", "http://localhost:28881");
        stringProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        stringProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        stringProducer = new KafkaProducer<>(stringProps);
    }

    protected void sendAvro(ProducerRecord<Object, Object> record) {
        try {
            // синхронная отправка сообщения
            final var task = avroProducer.send(record).get();
            System.out.printf("Sent Message, Offset %d%n", task.offset());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        avroProducer.close();
    }

    protected void sendString(ProducerRecord<Object, Object> record) {
        try {
            // синхронная отправка сообщения
            final var task = stringProducer.send(record).get();
            System.out.printf("Sent Message, Offset %d%n", task.offset());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
        stringProducer.close();
    }

    protected String randomKey() {
        return "key~" + Instant.now().getEpochSecond(); // случайный key
    }

}
