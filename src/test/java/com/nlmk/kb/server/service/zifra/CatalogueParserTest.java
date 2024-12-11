package com.nlmk.kb.server.service.zifra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.exception.ZifraMessageParserException;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.EnumOp;
import nlmk.l3.nsi.zifra.Reason;
import nlmk.l3.nsi.zifra.lineAttributes_record;
import nlmk.l3.nsi.zifra.pk;
import nlmk.l3.nsi.zifra.properties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CatalogueParserTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String getJsonFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    @Test
    void spCustomerParser() throws Exception {
        SpCustomerParser parser = new SpCustomerParser();
        assertThat(parser.getCatalogue()).isEqualTo(Catalogue.SP_CUSTOMER);

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpCustomerExample.json"),
                Reason.class
        );
        assertThat(obj).isNotNull();

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertThat(dto.getId()).isEqualTo("90515640-77bc-11ed-bf7c-f39ea38ca6e0");
        assertThat(dto.getCustomerId()).isEqualTo("111111111");
        assertThat(dto.getCustomerName()).isEqualTo("ТЕСТ ПОТРЕБИТЕЛЬ");
        assertThat(dto.getShortName()).isEqualTo("ТЕСТ");
        assertThat(dto.getDateBegin()).isNull();
        assertThat(dto.getDateEnd()).isNull();
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    void spCustomerGroupParser() throws Exception {
        SpCustomerGroupParser parser = new SpCustomerGroupParser();
        assertThat(parser.getCatalogue()).isEqualTo(Catalogue.SP_CUSTOMER_GROUP);

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpCustomerGroupExample.json"),
                Reason.class
        );
        assertThat(obj).isNotNull();

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertThat(dto.getId()).isEqualTo("987e00c0-6b00-11ed-97c4-2b96fc4bacbb");
        assertThat(dto.getGroupId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("ВР");
        assertThat(dto.getDateBegin()).isNull();
        assertThat(dto.getDateEnd()).isNull();
        assertThat(dto.getIsActive()).isTrue();
    }

    @Test
    void spGroupAndCustomerParser() throws Exception {
        SpGroupAndCustomerParser parser = new SpGroupAndCustomerParser();
        assertThat(parser.getCatalogue()).isEqualTo(Catalogue.SP_GROUP_AND_CUSTOMER);

        final var obj = objectMapper.readValue(
                getJsonFromPath("src/test/resources/json/SpGroupAndCustomerExample.json"),
                Reason.class
        );
        assertThat(obj).isNotNull();

        final var dto = parser.parse(obj.getPk(), obj.getData());
        assertThat(dto.getId()).isEqualTo("a09e7690-77bc-11ed-bf7c-f39ea38ca6e0");
        assertThat(dto.getGroupId()).isEqualTo("7a533980-6b00-11ed-97c4-2b96fc4bacbb");
        assertThat(dto.getCustomerId()).isEqualTo("90515640-77bc-11ed-bf7c-f39ea38ca6e0");
        assertThat(dto.getPriority()).isEqualTo(1);
        assertThat(dto.getDateBegin()).isNull();
        assertThat(dto.getDateEnd()).isNull();
        assertThat(dto.getIsActive()).isTrue();
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
                                .setDateBegin(new StringBuilder("2022-01-01"))
                                .setDateEnd(new StringBuilder("20YY-12-12")) // ошибка
                                .build())
                        .setLineAttributes(List.of(
                                lineAttributes_record.newBuilder().setAttrCode("code1")
                                        .setAttrName("имя 1").setAttrNameEng(new StringBuilder("name1"))
                                        .setAttrType("TEXT").setAttrValue(new StringBuilder("value1"))
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code2")
                                        .setAttrName("имя 2").setAttrNameEng(new StringBuilder("name2"))
                                        .setAttrType("NUMBER").setAttrValue(new StringBuilder("2"))
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code3")
                                        .setAttrName("Признак активности").setAttrNameEng(new StringBuilder("active"))
                                        .setAttrType("BOOLEAN").setAttrValue(new StringBuilder("true"))
                                        .setHashtagColumn(List.of()).build(),
                                lineAttributes_record.newBuilder().setAttrCode("code21")
                                        .setAttrName("имя 21").setAttrNameEng(new StringBuilder("name21"))
                                        .setAttrType("NUMBER").setAttrValue(new StringBuilder("2A")) // ошибка
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

        assertThat(reason.getPk().getLineId()).isEqualTo("111-222");
        assertThat(parser.getActive(attr)).isTrue();
        assertThat(parser.getAttrStringValueByName(attr, "name1")).isEqualTo("value1");
        assertThat(parser.getAttrStringValueByName(attr, "xyz")).isNull();
        assertThat(parser.getAttrIntegerValueByName(attr, "name2")).isEqualTo(2);
        assertThat(parser.getBeginDate(prop)).isNotNull();

        assertThatThrownBy(() -> parser.getEndDate(prop))
                .isInstanceOf(ZifraMessageParserException.class)
                .hasMessage("Значение даты \"20YY-12-12\" не соответствует шаблону \"yyyy-MM-dd\"");

        assertThatThrownBy(() -> parser.getAttrIntegerValueByName(attr, "name21"))
                .isInstanceOf(ZifraMessageParserException.class)
                .hasMessage("Ошибка преобразования строки \"2A\" в целое число");
    }

}
