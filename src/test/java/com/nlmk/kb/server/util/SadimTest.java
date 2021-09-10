package com.nlmk.kb.server.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.SadimJsonParser;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.sadim.Sadim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
public class SadimTest {

    @Autowired
    @Qualifier("sadimStreamApiParser")
    private SadimJsonParser parser;

    @Test
    void sadimJsonTest() throws IOException {
        String testString = "2021-04-28T16:29:29.612-03:00";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        Sadim value = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                .readValue(getClass().getClassLoader()
                        .getResourceAsStream("json/exampleFromSadim.json"), Sadim.class);

        System.out.println("---sadim: " + value);
        assertNotNull(value);
    }

    @Test
    void SadimStreamApiParserTest() throws FileNotFoundException {

     //   SadimJsonParser parser = new SadimStreamApiParser();
        val jsonString = getJsonFromPath("src/main/resources/json/sadim09052020_1.json");
       // val jsonString = getJsonFromPath("src/main/resources/json/sadimError.json");
        val param = parser.getParam(jsonString);

        log.info("--- param: " + param);

        assertNotNull(param);
        assertNotNull(param.get());
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
        FileInputStream fis = new FileInputStream(new File(path));
        String stringTooLong = IOUtils.toString(fis);

        return stringTooLong;
    }
}
