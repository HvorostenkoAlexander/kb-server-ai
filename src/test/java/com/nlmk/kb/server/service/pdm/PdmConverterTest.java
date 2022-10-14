package com.nlmk.kb.server.service.pdm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.nsi.TkNumDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.CommonConverterImpl;
import nlmk.l3.pdm.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PdmConverterTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final PdmDictionaryCreator pdmDictionaryCreator = new PdmDictionaryCreatorImpl(commonConverter);
    private final PdmDtoConverter pdmDtoConverter = new PdmDtoConverterImpl(commonConverter);

    private String getJsonFromPath(String path) throws IOException {
        return new String(Files.readAllBytes(Path.of(path)));
    }

    @Test
    void SpTkNumEmptyDateTest() throws Exception {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTkNum.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertNotNull(tkNumDto);
        assertNull(tkNumDto.getDateStart());
        assertNull(tkNumDto.getDateFinish());
    }

    @Test
    void SpTkNumOkDateTest() throws Exception {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTkNumWithDate.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(), tkNum.getOp(), tkNum.getPk(), tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        assertNotNull(tkNumDto);
        assertNotNull(tkNumDto.getDateStart());
        assertNotNull(tkNumDto.getDateFinish());
        assertEquals("2000-05-26T09:58:15.6117006+03:00", tkNumDto.getDateStart());
        assertEquals("2022-05-26T09:58:15.6117006+03:00", tkNumDto.getDateFinish());
    }

    @Test
    void fromMicrostructureTest() throws Exception {

        SpMicrostructure micro = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/Microstructure.json"),
                        SpMicrostructure.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                micro.getTs(), micro.getOp(), micro.getPk(), micro.getData()
        );

        assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapChemicalPropertiesTest() throws Exception {
        SpAsapChemicalProperties chP = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/AsapChemicalProperties.json"),
                        SpAsapChemicalProperties.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                chP.getTs(), chP.getOp(), chP.getPk(), chP.getData()
        );
        assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapMechPropertiesDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/AsapMechPropertiesDt.json"),
                        SpAsapMechPropertiesDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toAsapMechPropertiesDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("11ЮА",dto.getPrProdMark());
        assertEquals("ТУ 14-106-454-94",dto.getPrStandMark());
        assertEquals("4.00..8.00",dto.getPrThickUncoat().getSrcValue());
        assertEquals("Тест",dto.getPrAnnotation());
    }

    @Test
    void fromPhysMechPropAnisSteelStandTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/PhysMechPropAnisSteelStand.json"),
                        SpPhysMechPropAnisSteelStand.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toPhysMechPropAnisSteelStandDto(dictionary);

        assertNotNull(dto);
        assertEquals("11ЮА",dto.getPrProdMark());
        assertEquals("ТУ 14-106-454-94",dto.getPrStandMark());
        assertEquals("4.00..8.00",dto.getPrThickUncoat().getSrcValue());
        assertEquals("Тест",dto.getPrAnnotation());
    }

    @Test
    void fromTolEvennessDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolEvennessDt.json"),
                        SpTolEvennessDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolEvennessDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("ДТ 37.06", dto.getDt());
        assertEquals("IS 3024:2015",dto.getPrStandMark());
        assertEquals("(150..*", dto.getPrWidthGood().getSrcValue());
        assertEquals("", dto.getPrEvenness());
        assertEquals(3.0, dto.getPrEvennessTolMax());
        assertEquals(1.5, dto.getPrEvennessTolPerc());
        assertEquals("Тест",dto.getPrAnnotation());
    }

    @Test
    void fromTolThickDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolThickDt.json"),
                        SpTolThickDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolThickDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("100", dto.getRemoteId());
        assertEquals("ДТ 157.00", dto.getDt());
        assertEquals("0.35", dto.getPrThickUncoat().getSrcValue());
        assertEquals("*..0.016", dto.getLongThickDif());
        assertEquals("*..0.015", dto.getPrUnevenGauge());
        assertEquals("Тест",dto.getPrAnnotation());
    }

    @Test
    void fromTolWidthDtTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/TolWidthDt.json"),
                        SpTolWidthDt.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toTolWidthDtDto(dictionary);

        assertNotNull(dto);
        assertEquals("15", dto.getRemoteId());
        assertEquals("ДТ 37.06", dto.getDt());
        assertEquals("IS 3024:2015",dto.getPrStandMark());
        assertEquals("(1000..1020]",dto.getPrWidthGood().getSrcValue());
        assertEquals(1.5, dto.getPrWidthTolMax());
        assertEquals("*..0.9",dto.getSickleShape().getSrcValue());
        assertEquals("*..0.025", dto.getBurr().getSrcValue());
        assertEquals("Тест",dto.getPrAnnotation());
    }

    @Test
    void fromSpTolThickTest() throws Exception {
        final var obj = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/test/resources/json/SpTolThick.json"),
                        SpTolThick.class
                );

        final var dictionary = pdmDictionaryCreator.createPdmDictionary(
                obj.getTs(), obj.getOp(), obj.getPk(), obj.getData()
        );

        final var dto = pdmDtoConverter.toThicknessTkLimitDto(dictionary);

        assertNotNull(dto);
        assertEquals("7999", dto.getRemote_id());
        assertEquals("(2.50..3.00]", dto.getPrThickGood().getSrcValue());
        assertEquals("1.55", dto.getLongThickDif());
        assertEquals("", dto.getPrUnevenGauge());
        assertEquals("Тест",dto.getPrAnnotation());
    }
}
