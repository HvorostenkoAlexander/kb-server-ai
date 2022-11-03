package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.service.sadim.SadimJsonElement;
import com.nlmk.kb.server.service.sadim.SadimJsonParserImpl;
import com.nlmk.kb.server.service.sadim.SadimJsonParser;
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
    private final SadimJsonParser parser = new SadimJsonParserImpl(commonConverter);

    @Test
    void enumFiled() {
        Assertions.assertNull(SadimJsonElement.fromName(null));
        Assertions.assertNull(SadimJsonElement.fromName(""));
        Assertions.assertNull(SadimJsonElement.fromName(" "));
        Assertions.assertNull(SadimJsonElement.fromName("A"));

        Assertions.assertEquals(SadimJsonElement.LOT_NO, SadimJsonElement.fromName("lot_no"));
    }

    @Test
    void parsing() throws FileNotFoundException {
        final var sadimJson = getJsonFromPath();
        final var attestationParam = parser.getParam(sadimJson);

        Assertions.assertTrue(attestationParam.isPresent());
        Assertions.assertEquals("0001020210520101736225770", attestationParam.get().getPrimeId());
        Assertions.assertEquals(40233, attestationParam.get().getLotNo());
        Assertions.assertEquals(2111357, attestationParam.get().getMeltNo());
        Assertions.assertEquals(825, attestationParam.get().getT12Min());
        Assertions.assertEquals(865, attestationParam.get().getT12Max());
        Assertions.assertEquals(615, attestationParam.get().getTcmMin());
        Assertions.assertEquals(665, attestationParam.get().getTcmMax());
        Assertions.assertEquals(100, attestationParam.get().getPbi());
        Assertions.assertEquals(25, attestationParam.get().getProfFact());
        Assertions.assertEquals(5, attestationParam.get().getWedgeFact());
        Assertions.assertEquals(100, attestationParam.get().getPh1sgp());
        Assertions.assertEquals("26.62", attestationParam.get().getPh12sgp());
        Assertions.assertEquals(90.27, attestationParam.get().getPh23sgp());
        Assertions.assertEquals(4, attestationParam.get().getSqcCritMax());
        Assertions.assertEquals("5.0;9.0;6.0;4.0;2.0;12.0;4.0;2.0", attestationParam.get().getLclThckng());
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
        final var jsonString = getJsonFromPath("src/test/resources/json/sadim09052020_1.json");
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
