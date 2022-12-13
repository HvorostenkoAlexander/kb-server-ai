package com.nlmk.kb.server.testing;

import nlmk.l3.nsi.zifra.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.List;
import java.util.Properties;

@Disabled("hand sender")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SendZifraMessageToKafkaTest {

    KafkaProducer<Object, Object> avroProducer;
    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private static final String ZIFRA_CUSTOMER_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-customer.0";
    private static final String ZIFRA_CUSTOMER_GROUP_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-customer-group.0";
    private static final String ZIFRA_GROUP_AND_CUSTOMER_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-group-and-customer.0";

    @BeforeAll
    void setUp() {
        final var avroProps = new Properties();
        avroProps.put("bootstrap.servers", "localhost:29092");
        avroProps.put("schema.registry.url", "http://localhost:28881");
        avroProps.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProps.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        avroProducer = new KafkaProducer<>(avroProps);
    }

    private void sendAvro(ProducerRecord<Object, Object> record) {
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

    private String randomKey() {
        return "key~" + Instant.now().getEpochSecond(); // случайный key
    }

    @Test
    void spCustomerSend() {
        Reason value = Reason.newBuilder()
                .setOp(EnumOp.I)
                .setTs("2022-10-10T12:26:11.563+00:00")
                .setPk(pk.newBuilder().setSystemCode("54").setLineId("111-222").build())
                .setData(Data.newBuilder()
                        .setCatalogId("catalogId").setCatalogCode("catalogCode")
                        .setHashtagLine(List.of()).setHashtagCatalog(List.of())
                        .setProperties(properties.newBuilder()
                                .setCron("12/34/56")
                                .setDateChange("----")
                                .setDateBegin("2022-01-01")
                                .setDateEnd("2022-12-31")
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1")
                                        .setAttrName("Идентификатор").setAttrNameEng("customerId")
                                        .setAttrType("TEXT").setAttrValue("1234")
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2")
                                        .setAttrName("Наименование").setAttrNameEng("customerName")
                                        .setAttrType("TEXT").setAttrValue("Тестовый потребитель 1234")
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3")
                                        .setAttrName("Признак активности").setAttrNameEng("active")
                                        .setAttrType("BOOLEAN").setAttrValue("true")
                                        .setHashtagColumn(List.of()).build()
                        ))
                        .build())
                .build();

        ProducerRecord<Object, Object> record = new ProducerRecord<>(ZIFRA_CUSTOMER_TOPIC, randomKey(), value);

        sendAvro(record);
    }

}
