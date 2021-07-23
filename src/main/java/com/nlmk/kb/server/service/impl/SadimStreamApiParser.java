package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.SadimJsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("sadimStreamApiParser")
@RequiredArgsConstructor
public class SadimStreamApiParser implements SadimJsonParser {

    private final CommonConverter converter;

    @Override
    public Optional<PreAttestationParam> getParam(String jsonString) {
        String sadimDate=null;
        val paramBuilder = PreAttestationParam.builder();

        try (JsonParser jParser = new JsonFactory().createParser(jsonString);) {

            while (jParser.nextToken() != null) {
                String fieldname = jParser.getCurrentName();

                if ("time_rolling".equals(fieldname)){
                    jParser.nextToken();
                    sadimDate = jParser.getText();
                }

                if ("PRIME_ID".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.primeId(jParser.getText());
                }
                if ("t12_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Min(converter.parsToDouble(jParser.getText()));
                }
                if ("t12_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Max(converter.parsToDouble(jParser.getText()));
                }
                if ("tcm_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMin(converter.parsToDouble(jParser.getText()));
                }
                if ("tcm_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMax(converter.parsToDouble(jParser.getText()));
                }
                if ("PBI".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.pbi(converter.parsToDouble(jParser.getText()));
                }
                if ("ProfFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.profFact(converter.parsToDouble(jParser.getText()));
                }
                if ("WedgeFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.wedgeFact(converter.parsToDouble(jParser.getText()));
                }
                if ("SQC_CRIT_MAX".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.sqcCritMax(converter.parsToDouble(jParser.getText()));
                }
                if ("PH_1SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph1sgp(converter.parsToDouble(jParser.getText()));
                }
                if ("PH_12SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph12sgp(jParser.getText());
                }
                if ("PH_23SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph23sgp(converter.parsToDouble(jParser.getText()));
                }
                if ("estimate".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.estimate(converter.parsToInteger(jParser.getText()));
                }
                if ("lclThckng".equals(fieldname) && jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    paramBuilder.lclThckng(getArrayFromLclThckngSadim(jParser));
                }
            }
        } catch (IOException | NumberFormatException ioe) {
            throw new SadimJsonProcessingException("Не удалось обработать json от SADIM: " + ioe.getMessage());
        }
        val param = paramBuilder.build();

        if (sadimDate!=null) {
            log.info("Сведения SADIM с primeId: [{}] от: [{}]",param.getPrimeId(), sadimDate);
        }

        if (param.getPrimeId() != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }

    private List<Double> getArrayFromLclThckngSadim(JsonParser jParser) throws IOException {
        List<List<Double>> values = new ArrayList<>();

        while (!("lclThckng".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_OBJECT)) {
            jParser.nextToken();

            if ("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.START_ARRAY) {

                while (!("values".equals(jParser.getCurrentName()) && jParser.getCurrentToken() == JsonToken.END_ARRAY)) {

                    if (jParser.nextToken() == JsonToken.START_ARRAY) {
                        List<Double> onePare = new ArrayList<>();
                        while (jParser.nextToken() != JsonToken.END_ARRAY) {
                            onePare.add(converter.parsToDouble(jParser.getText()));
                        }
                        values.add(onePare);
                    }
                }
            }
        }
        return values.stream().map(value -> value.get(0)).collect(Collectors.toList());
    }
}
