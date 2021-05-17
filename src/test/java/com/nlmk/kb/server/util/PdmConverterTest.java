package com.nlmk.kb.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import io.micrometer.core.instrument.util.IOUtils;
import nlmk.l3.pdm.SpAsapChemicalProperties;
import nlmk.l3.pdm.SpMicrostructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

public class PdmConverterTest {

    @Test
    void fromMicrostructureTest() throws FileNotFoundException, JsonProcessingException {

        SpMicrostructure micro = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/Microstructure.json"),
                        SpMicrostructure.class
                );

        PdmDictionary pdmDictionary = PdmConverter.fromSpMicrostructure(micro);

        Assertions.assertNotNull(pdmDictionary);
    }

    @Test
    void fromAsapChemicalPropertiesTest() throws FileNotFoundException, JsonProcessingException {

        SpAsapChemicalProperties chP = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getJsonFromPath("src/main/resources/json/AsapChemicalProperties.json"),
                        SpAsapChemicalProperties.class
                );

        PdmDictionary pdmDictionary = PdmConverter.fromSpAsapChemicalProperties(chP);

        Assertions.assertNotNull(pdmDictionary);
    }

    private String getJsonFromPath(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(new File(path));
        String stringTooLong = IOUtils.toString(fis);

        return stringTooLong;
    }
}
