package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.SadimJsonParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("sadimStreamApiParser")
public class SadimStreamApiParser implements SadimJsonParser {

    @Override
    public Optional<PreAttestationParam> getParam(String jsonString) {

        val paramBuilder = PreAttestationParam.builder();

        try (JsonParser jParser = new JsonFactory().createParser(jsonString);) {

            while (jParser.nextToken() != null) {
                String fieldname = jParser.getCurrentName();

                if ("PRIME_ID".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.primeId(jParser.getText());
                }
                if ("t12_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Min(jParser.getDoubleValue());
                }
                if ("t12_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Max(jParser.getDoubleValue());
                }
                if ("tcm_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMin(jParser.getDoubleValue());
                }
                if ("tcm_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMax(jParser.getDoubleValue());
                }
                if ("PBI".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.pbi(jParser.getDoubleValue());
                }
                if ("ProfFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.profFact(jParser.getDoubleValue());
                }
                if ("WedgeFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.wedgeFact(jParser.getDoubleValue());
                }
                if ("SQC_CRIT_MAX".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.sqcCritMax(jParser.getDoubleValue());
                }
                if ("PH_1SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph1sgp(jParser.getDoubleValue());
                }
                if ("PH_12SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph12sgp(jParser.getDoubleValue());
                }
                if ("PH_23SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph23sgp(jParser.getDoubleValue());
                }
                if ("estimate".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.estimate(jParser.getIntValue());
                }
                if ("lclThckng".equals(fieldname) && jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    paramBuilder.lclThckng(getArrayFromLclThckngSadim(jParser));
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException("Не удалось обработать json от SADIM: " + ioe.getMessage());
        }
        val param = paramBuilder.build();

        if (param.getPrimeId() != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }

    private Double[] getArrayFromLclThckngSadim(JsonParser jParser) throws IOException {
        List<List<Double>> values = new ArrayList<>();

        while (!("lclThckng".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_OBJECT)) {
            jParser.nextToken();

            if ("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.START_ARRAY) {

                while (!("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_ARRAY)) {

                    if (jParser.nextToken() == JsonToken.START_ARRAY) {
                        List<Double> onePare = new ArrayList<>();
                        while (jParser.nextToken() != JsonToken.END_ARRAY) {
                            onePare.add(jParser.getDoubleValue());
                        }
                        values.add(onePare);
                    }
                }
            }
        }
        return values.stream().map(value -> value.get(0)).toArray(Double[]::new);
    }
}
