package com.nlmk.kb.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.nsi.TkNumDto;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import com.nlmk.kb.server.exception.DateTimeParseException;
import com.nlmk.kb.server.service.PdmDictionaryCreator;
import com.nlmk.kb.server.service.PdmDtoConverter;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpMicrostructure;
import nlmk.l3.pdm.SpTkNum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
public class PdmConverterTest {

    @Autowired
    private PdmDictionaryCreator pdmDictionaryCreator;

    @Autowired
    private PdmDtoConverter pdmDtoConverter;

    @Test
    void SpTkNumEmptyDateTest() throws FileNotFoundException, JsonProcessingException {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/SpTkNum.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(),tkNum.getOp(),tkNum.getPk(),tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        log.info("--- tkNumDto: {}",tkNumDto);

        assertNotNull(tkNumDto);
        assertNull(tkNumDto.getDateStart());
        assertNull(tkNumDto.getDateFinish());
    }

    @Test
    void SpTkNumOkDateTest() throws FileNotFoundException, JsonProcessingException {
        SpTkNum tkNum = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/SpTkNumWithDate.json"),
                        SpTkNum.class
                );

        PdmDictionary dictionary = pdmDictionaryCreator.createPdmDictionary(
                tkNum.getTs(),tkNum.getOp(),tkNum.getPk(),tkNum.getData()
        );

        TkNumDto tkNumDto = pdmDtoConverter.toTkNumDto(dictionary);

        log.info("--- tkNumDto: {}",tkNumDto);

        assertNotNull(tkNumDto);
        assertNotNull(tkNumDto.getDateStart());
        assertNotNull(tkNumDto.getDateFinish());
        assertEquals("2000-05-26T09:58:15.6117006+03:00",tkNumDto.getDateStart());
        assertEquals("2022-05-26T09:58:15.6117006+03:00",tkNumDto.getDateFinish());
    }

    @Test
    void fromMicrostructureTest() throws FileNotFoundException, JsonProcessingException {

        SpMicrostructure micro = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/Microstructure.json"),
                        SpMicrostructure.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                micro.getTs(),micro.getOp(),micro.getPk(),micro.getData()
        );

        assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapChemicalPropertiesTest() throws FileNotFoundException, JsonProcessingException {

        SpAsapChemicalProperties chP = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/AsapChemicalProperties.json"),
                        SpAsapChemicalProperties.class
                );

        PdmDictionary pdmDictionary = pdmDictionaryCreator.createPdmDictionary(
                chP.getTs(),chP.getOp(),chP.getPk(),chP.getData()
        );
        assertNotNull(pdmDictionary);
    }

    private String getJsonFromPath(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File(path));
        String stringTooLong = IOUtils.toString(fis);

        return stringTooLong;
    }
}
