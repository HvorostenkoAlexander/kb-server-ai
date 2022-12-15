package com.nlmk.kb.server.service.zifra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.exception.ZifraMessageParserException;
import nlmk.l3.nsi.zifra.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogueParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getJsonFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    @Test
    void spCustomerParser() throws Exception {
        SpCustomerParser parser = new SpCustomerParser();
        assertEquals(Catalogue.SP_CUSTOMER, parser.getCatalogue());

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpCustomerExample.json"),
                Reason.class
        );
        assertNotNull(obj);

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertNotNull(dto);
        assertEquals("90515640-77bc-11ed-bf7c-f39ea38ca6e0", dto.getId());
        assertEquals("111111111", dto.getCustomerId());
        assertEquals("ТЕСТ ПОТРЕБИТЕЛЬ", dto.getCustomerName());
        assertEquals("ТЕСТ", dto.getShortName());
        assertNull(dto.getDateBegin());
        assertNull(dto.getDateEnd());
        assertTrue(dto.getIsActive());
    }

    @Test
    void spCustomerGroupParser() throws Exception {
        SpCustomerGroupParser parser = new SpCustomerGroupParser();
        assertEquals(Catalogue.SP_CUSTOMER_GROUP, parser.getCatalogue());

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpCustomerGroupExample.json"),
                Reason.class
        );
        assertNotNull(obj);

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertNotNull(dto);
        assertEquals("987e00c0-6b00-11ed-97c4-2b96fc4bacbb", dto.getId());
        assertEquals(3, dto.getGroupId());
        assertEquals("ВР", dto.getName());
        assertNull(dto.getDateBegin());
        assertNull(dto.getDateEnd());
        assertTrue(dto.getIsActive());
    }

    @Test
    void spGroupAndCustomerParser() throws Exception {
        SpGroupAndCustomerParser parser = new SpGroupAndCustomerParser();
        assertEquals(Catalogue.SP_GROUP_AND_CUSTOMER, parser.getCatalogue());

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpGroupAndCustomerExample.json"),
                Reason.class
        );
        assertNotNull(obj);

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertNotNull(dto);
        assertEquals("a09e7690-77bc-11ed-bf7c-f39ea38ca6e0", dto.getId());
        assertEquals("7a533980-6b00-11ed-97c4-2b96fc4bacbb", dto.getGroupId());
        assertEquals("90515640-77bc-11ed-bf7c-f39ea38ca6e0", dto.getCustomerId());
        assertEquals(1, dto.getPriority());
        assertNull(dto.getDateBegin());
        assertNull(dto.getDateEnd());
        assertTrue(dto.getIsActive());
    }

    Reason prepareTestObject() {
        return Reason.newBuilder()
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
                                .setDateEnd("20YY-12-12") // ошибка
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1")
                                        .setAttrName("имя 1").setAttrNameEng("name1")
                                        .setAttrType("TEXT").setAttrValue("value1")
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2")
                                        .setAttrName("имя 2").setAttrNameEng("name2")
                                        .setAttrType("NUMBER").setAttrValue("2")
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3")
                                        .setAttrName("Признак активности").setAttrNameEng("active")
                                        .setAttrType("BOOLEAN").setAttrValue("true")
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code21")
                                        .setAttrName("имя 21").setAttrNameEng("name21")
                                        .setAttrType("NUMBER").setAttrValue("2A") // ошибка
                                        .setHashtagColumn(List.of()).build()
                        ))
                        .build())
                .build();
    }

    @Test
    void parserTest() {
        CatalogueParser<Object> parser = new CatalogueParser<>() {
            @Override
            public Catalogue getCatalogue() {
                return null;
            }

            @Override
            public Object parse(pk pk, Data data) {
                return null;
            }
        };

        final var reason = prepareTestObject();
        final var attr = reason.getData().getLineAttributes();
        final var prop = reason.getData().getProperties();

        assertEquals("111-222", reason.getPk().getLineId());
        assertTrue(parser.getActive(attr));
        assertEquals("value1", parser.getAttrStringValueByName(attr, "name1"));
        assertNull(parser.getAttrStringValueByName(attr, "xyz"));
        assertEquals(2, parser.getAttrIntegerValueByName(attr, "name2"));
        assertEquals(1_640_977_200_000L, parser.getBeginDate(prop).getTime());

        final var err1 = assertThrows(ZifraMessageParserException.class, () -> parser.getEndDate(prop));
        assertEquals("Значение даты \"20YY-12-12\" не соответствует шаблону \"yyyy-MM-dd\"", err1.getMessage());

        final var err2 = assertThrows(ZifraMessageParserException.class, () -> parser.getAttrIntegerValueByName(attr, "name21"));
        assertEquals("Ошибка преобразования строки \"2A\" в целое число", err2.getMessage());
    }

}
