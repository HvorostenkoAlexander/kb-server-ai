package com.nlmk.kb.server.service;

import com.nlmk.kb.server.service.impl.CommonConverterImpl;
import com.nlmk.kb.server.service.impl.SadimStreamApiParser;
import io.micrometer.core.instrument.util.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

class SadimMessageServiceTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final SadimJsonParser sadimJsonParser = new SadimStreamApiParser(commonConverter);

    @Test
    void parsing() throws FileNotFoundException {
        final var sadimJson = getJsonFromPath();
        final var attestationParam = sadimJsonParser.getParam(sadimJson);

        Assertions.assertTrue(attestationParam.isPresent());
        Assertions.assertEquals("0001020210520101736225770", attestationParam.get().getPrimeId());
    }

    private String getJsonFromPath() throws FileNotFoundException {
        final String validSadimFilePath = "src/test/resources/json/sadim09052021.json";
        FileInputStream fis = new FileInputStream(validSadimFilePath);
        return IOUtils.toString(fis);
    }

}
