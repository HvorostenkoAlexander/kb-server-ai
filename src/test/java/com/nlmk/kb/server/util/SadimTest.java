package com.nlmk.kb.server.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.SadimJsonParser;
import com.nlmk.kb.server.service.impl.SadimStreamApiParser;
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
import java.util.Arrays;
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
       // val jsonString = getJsonFromPath("src/main/resources/json/sadim09052020_1.json");
        val jsonString = getJsonFromPath("src/main/resources/json/sadimError.json");
        val param = parser.getParam(jsonString);

        log.info("--- param: " + param);

        assertNotNull(param);
        assertNotNull(param.get());
    }

    void test() throws FileNotFoundException {

        val jsonString = getJsonFromPath("src/main/resources/json/sadimError.json");
        val paramBuilder = PreAttestationParam.builder();

        try (JsonParser jParser = new JsonFactory().createParser(jsonString);) {

            while (jParser.nextToken() != null) {
                String fieldname = jParser.getCurrentName();
                // System.out.println("1 while: " + jParser.currentName() + "; " + jParser.getText());

                if ("PRIME_ID".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- PRIME_ID: " + jParser.getText());
                    paramBuilder.primeId(jParser.getText());
                }
                if ("t12_min".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- t12_min: " + jParser.getText());
                    paramBuilder.t12Min(parsToDouble(jParser.getText()));
                }
                if ("t12_max".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- t12_max: " + jParser.getText());
                    paramBuilder.t12Max(parsToDouble(jParser.getText()));
                }
                if ("tcm_min".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- tcm_min: " + jParser.getText());
                    paramBuilder.tcmMin(parsToDouble(jParser.getText()));
                }
                if ("tcm_max".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- tcm_max: " + jParser.getText());
                    paramBuilder.tcmMax(parsToDouble(jParser.getText()));
                }
                if ("PBI".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- PBI: " + jParser.getText());
                    paramBuilder.pbi(parsToDouble(jParser.getText()));
                }
                if ("ProfFact".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- ProfFact: " + jParser.getText());
                    paramBuilder.profFact(parsToDouble(jParser.getText()));
                }
                if ("WedgeFact".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- WedgeFact: " + jParser.getText());
                    paramBuilder.wedgeFact(parsToDouble(jParser.getText()));
                }
                if ("SQC_CRIT_MAX".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- SQC_CRIT_MAX: " + jParser.getText());
                    paramBuilder.sqcCritMax(parsToDouble(jParser.getText()));
                }
                if ("PH_1SGP".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- PH_1SGP: " + jParser.getText());
                    paramBuilder.ph1sgp(parsToDouble(jParser.getText()));
                }
                if ("PH_12SGP".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- PH_12SGP: " + jParser.getText());
                    paramBuilder.ph12sgp(jParser.getText());
                }
                if ("PH_23SGP".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- PH_23SGP: " + jParser.getText());
                    paramBuilder.ph23sgp(parsToDouble(jParser.getText()));
                }
                if ("estimate".equals(fieldname)) {
                    jParser.nextToken();
                    System.out.println("--- estimate: " + jParser.getText());
                    paramBuilder.estimate(parsToInteger(jParser.getText()));
                }
                if ("lclThckng".equals(fieldname) && jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    paramBuilder.lclThckng(getArrayFromLclThckngSadim(jParser));
                }
            }
        } catch (IOException ioe) {// todo обработать exception
            System.out.println("уппс: " + ioe);
        }
        val attestationParam = paramBuilder.build();
        System.out.println("--- AttestationParam: " + attestationParam);
        //System.out.println("---- lclThckng: " + Arrays.toString(attestationParam.getLclThckng()));
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
