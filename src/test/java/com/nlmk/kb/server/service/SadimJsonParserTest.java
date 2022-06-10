package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.service.impl.CommonConverterImpl;
import com.nlmk.kb.server.service.impl.SadimStreamApiParser;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import nlmk.sadim.Sadim;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class SadimJsonParserTest {

    private final CommonConverter commonConverter = new CommonConverterImpl();
    private final SadimJsonParser parser = new SadimStreamApiParser(commonConverter);

    @Test
    void parsing() throws FileNotFoundException {
        final var sadimJson = getJsonFromPath();
        final var attestationParam = parser.getParam(sadimJson);

        Assertions.assertTrue(attestationParam.isPresent());
        Assertions.assertEquals("0001020210520101736225770", attestationParam.get().getPrimeId());
    }

    private String getJsonFromPath() throws FileNotFoundException {
        final String validSadimFilePath = "src/test/resources/json/sadim09052021.json";
        FileInputStream fis = new FileInputStream(validSadimFilePath);
        return IOUtils.toString(fis);
    }

    @Test
    void sadimJsonTest() throws IOException {
        Sadim value = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getClass().getClassLoader()
                        .getResourceAsStream("json/exampleFromSadim.json"), Sadim.class);
        // System.out.println("---sadim: " + value);
        assertNotNull(value);
    }

    @Test
    void SadimStreamApiParserTest() throws FileNotFoundException {
        final var jsonString = getJsonFromPath("src/main/resources/json/sadim09052020_1.json");
        final var param = parser.getParam(jsonString);
        log.info("--- param: " + param);
        assertTrue(param.isPresent());
    }

    private List<Double> getArrayFromLclThckngSadim(JsonParser jParser) throws IOException {
        System.out.println("! checkLclThckng: " + jParser.currentName() + "; " + jParser.getText());
        List<List<Double>> values = new ArrayList<>();

        while (!("lclThckng".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_OBJECT)) {
            jParser.nextToken();
            System.out.println("1--- currentName:" + jParser.getCurrentName() + "; token: " + jParser.getCurrentToken());
            if ("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.START_ARRAY) {
                System.out.println("2--- currentName:" + jParser.getCurrentName() + "; token: " + jParser.getCurrentToken());
                while (!("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_ARRAY)) {

                    if (jParser.nextToken() == JsonToken.START_ARRAY) {
                        List<Double> onePare = new ArrayList<>();
                        while (jParser.nextToken() != JsonToken.END_ARRAY) {
                            System.out.println("--- ---: " + jParser.currentName() + "; " + jParser.getText());
                            onePare.add(parsToDouble(jParser.getText()));
                        }
                        values.add(onePare);
                    }
                }
            }
        }
        return values.stream().map(value -> value.get(0)).collect(Collectors.toList());
    }

    private Double parsToDouble(String s) {
        Double d = null;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException | NullPointerException e) {
            log.debug("--- parsToDouble: " + e);
        }
        return d;
    }

    private Integer parsToInteger(String s) {
        Integer i = null;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            log.debug("--- parsToInteger: " + e);
        }
        return i;
    }

    private String getJsonFromPath(String path) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(path);
        return IOUtils.toString(fis);
    }

}
