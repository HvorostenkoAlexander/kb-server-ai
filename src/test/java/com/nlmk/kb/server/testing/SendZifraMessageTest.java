package com.nlmk.kb.server.testing;

import com.nlmk.kb.server.service.zifra.Catalogue;
import nlmk.l3.nsi.zifra.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.*;

import java.util.List;

@Disabled("hand sender")
class SendZifraMessageTest extends SendMessageToKafka {

    // одна комбинация: тема + схема (версия схемы привязана к теме!)
    private static final String ZIFRA_CUSTOMER_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-customer.0";
    private static final String ZIFRA_CUSTOMER_GROUP_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-customer-group.0";
    private static final String ZIFRA_GROUP_AND_CUSTOMER_TOPIC = "000-1.l3-nsi-zifra.cdc.sp-l2code-group-and-customer.0";

    private Reason prepareSpCustomer(EnumOp operation,
                                     String guid,
                                     String customerId,
                                     String customerName,
                                     boolean active) {
        return Reason.newBuilder()
                .setOp(operation)
                .setTs("2022-12-14T12:26:11.563+05:00")
                .setPk(pk.newBuilder().setSystemCode("54").setLineId(guid).build())
                .setData(Data.newBuilder()
                        .setCatalogId("catalogId").setCatalogCode(Catalogue.SP_CUSTOMER.getCode())
                        .setHashtagLine(List.of()).setHashtagCatalog(List.of())
                        .setProperties(properties.newBuilder().setCron("12/34/56").setDateChange("----")
                                .setDateBegin("2022-01-01").setDateEnd("2022-12-31")
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1").setAttrType("TEXT")
                                        .setAttrName("Идентификатор").setHashtagColumn(List.of())
                                        .setAttrNameEng("customerId").setAttrValue(customerId)
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2").setAttrType("TEXT")
                                        .setAttrName("Наименование").setHashtagColumn(List.of())
                                        .setAttrNameEng("customerName").setAttrValue(customerName)
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3").setAttrType("BOOLEAN")
                                        .setAttrName("Признак активности").setHashtagColumn(List.of())
                                        .setAttrNameEng("active").setAttrValue(Boolean.toString(active))
                                        .build()
                        ))
                        .build())
                .build();
    }

    private Reason prepareSpCustomerGroup(EnumOp operation,
                                          String guid,
                                          Integer groupId,
                                          String groupName,
                                          boolean active) {
        return Reason.newBuilder()
                .setOp(operation)
                .setTs("2022-12-14T12:36:14.563+05:00")
                .setPk(pk.newBuilder().setSystemCode("54").setLineId(guid).build())
                .setData(Data.newBuilder()
                        .setCatalogId("catalogId").setCatalogCode(Catalogue.SP_CUSTOMER_GROUP.getCode())
                        .setHashtagLine(List.of()).setHashtagCatalog(List.of())
                        .setProperties(properties.newBuilder().setCron("12/34/56").setDateChange("----")
                                .setDateBegin("2022-01-01").setDateEnd("2022-12-31")
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1").setAttrType("NUMBER")
                                        .setAttrName("Идентификатор группы").setHashtagColumn(List.of())
                                        .setAttrNameEng("groupId").setAttrValue(groupId.toString())
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2").setAttrType("TEXT")
                                        .setAttrName("Наименование группы").setHashtagColumn(List.of())
                                        .setAttrNameEng("name").setAttrValue(groupName)
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3").setAttrType("BOOLEAN")
                                        .setAttrName("Признак активности").setHashtagColumn(List.of())
                                        .setAttrNameEng("active").setAttrValue(Boolean.toString(active))
                                        .build()
                        ))
                        .build())
                .build();
    }

    private Reason prepareSpGroupAndCustomer(EnumOp operation,
                                             String guid,
                                             String guidGroup,
                                             String guidCustomer,
                                             Integer priority,
                                             boolean active) {
        return Reason.newBuilder()
                .setOp(operation)
                .setTs("2022-12-14T12:42:18.563+05:00")
                .setPk(pk.newBuilder().setSystemCode("54").setLineId(guid).build())
                .setData(Data.newBuilder()
                        .setCatalogId("catalogId").setCatalogCode(Catalogue.SP_GROUP_AND_CUSTOMER.getCode())
                        .setHashtagLine(List.of()).setHashtagCatalog(List.of())
                        .setProperties(properties.newBuilder().setCron("12/34/56").setDateChange("----")
                                .setDateBegin("2022-01-01").setDateEnd("2022-12-31")
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1").setAttrType("DIRECTORY_ITEM_FIELD")
                                        .setAttrName("GUID группы").setHashtagColumn(List.of())
                                        .setAttrNameEng("groupId").setAttrValue(guidGroup)
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2").setAttrType("DIRECTORY_ITEM_FIELD")
                                        .setAttrName("GUID потребителя").setHashtagColumn(List.of())
                                        .setAttrNameEng("customerId").setAttrValue(guidCustomer)
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3").setAttrType("BOOLEAN")
                                        .setAttrName("Признак активности").setHashtagColumn(List.of())
                                        .setAttrNameEng("active").setAttrValue(Boolean.toString(active))
                                        .build(),
                                lineAttributes_record.newBuilder().setAttrCode("code4").setAttrType("NUMBER")
                                        .setAttrName("Приоритет потребителя").setHashtagColumn(List.of())
                                        .setAttrNameEng("priority").setAttrValue(priority.toString())
                                        .build()
                        ))
                        .build())
                .build();
    }

    // SpCustomer

    @Test
    void sendSpCustomer1() {
        sendAvro(new ProducerRecord<>(ZIFRA_CUSTOMER_TOPIC, randomKey(), prepareSpCustomer(
                EnumOp.I, "guid-c-1", "c-1", "customer1", true
        )));
    }

    @Test
    void sendSpCustomer2() {
        sendAvro(new ProducerRecord<>(ZIFRA_CUSTOMER_TOPIC, randomKey(), prepareSpCustomer(
                EnumOp.U, "guid-c-2", "c-2", "customer2", false
        )));
    }

    @Test
    void sendSpCustomer3() {
        sendAvro(new ProducerRecord<>(ZIFRA_CUSTOMER_TOPIC, randomKey(), prepareSpCustomer(
                EnumOp.U, "guid-c-3", "c-3", "customer3", true
        )));
    }

    // SpCustomerGroup

    @Test
    void sendSpCustomerGroup1() {
        sendAvro(new ProducerRecord<>(ZIFRA_CUSTOMER_GROUP_TOPIC, randomKey(), prepareSpCustomerGroup(
                EnumOp.I, "guid-g-1", 1, "group1", true
        )));
    }

    @Test
    void sendSpCustomerGroup2() {
        sendAvro(new ProducerRecord<>(ZIFRA_CUSTOMER_GROUP_TOPIC, randomKey(), prepareSpCustomerGroup(
                EnumOp.U, "guid-g-2", 2, "group2", false
        )));
    }

    // SpGroupAndCustomer: соединение SpCustomer и SpCustomerGroup по GUID (id записи)

    @Test
    void sendSpGroupAndCustomer1() {
        sendAvro(new ProducerRecord<>(ZIFRA_GROUP_AND_CUSTOMER_TOPIC, randomKey(), prepareSpGroupAndCustomer(
                EnumOp.I, "guid-gac-1", "guid-g-1", "guid-c-1", 1, true
        )));
    }

    @Test
    void sendSpGroupAndCustomer2() {
        sendAvro(new ProducerRecord<>(ZIFRA_GROUP_AND_CUSTOMER_TOPIC, randomKey(), prepareSpGroupAndCustomer(
                EnumOp.U, "guid-gac-2", "guid-g-2", "guid-c-1", 2, false
        )));
    }

    @Test
    void sendSpGroupAndCustomer3() {
        sendAvro(new ProducerRecord<>(ZIFRA_GROUP_AND_CUSTOMER_TOPIC, randomKey(), prepareSpGroupAndCustomer(
                EnumOp.I, "guid-gac-3", "guid-g-1", "guid-c-2", 1, true
        )));
    }

    @Test
    void sendSpGroupAndCustomer4() {
        sendAvro(new ProducerRecord<>(ZIFRA_GROUP_AND_CUSTOMER_TOPIC, randomKey(), prepareSpGroupAndCustomer(
                EnumOp.I, "guid-gac-4", "guid-g-2", "guid-c-3", 1, true
        )));
    }

}
