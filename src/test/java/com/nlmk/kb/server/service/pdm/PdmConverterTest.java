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

}
