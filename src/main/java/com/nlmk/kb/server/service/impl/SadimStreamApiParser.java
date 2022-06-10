package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.nlmk.attestation.product.api.SadimMessageDto;
import com.nlmk.kb.server.exception.SadimJsonProcessingException;
import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.SadimJsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SadimStreamApiParser implements SadimJsonParser {

    private final CommonConverter converter;

    @Override
    public Optional<SadimMessageDto.ParamDto> getParam(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            log.warn("jsonString is blank.");
            return Optional.empty();
        }

        String sadimDate = null;
        final var paramBuilder = SadimMessageDto.ParamDto.builder();

        try (JsonParser jParser = new JsonFactory().createParser(jsonString)) {
            while (jParser.nextToken() != null) {
                String fieldname = jParser.getCurrentName();

                if ("time_rolling".equals(fieldname)) {
                    jParser.nextToken();
                    sadimDate = jParser.getText();
                }

                if ("PRIME_ID".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.primeId(jParser.getText());
                }
                if ("t12_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Min(converter.parseToDouble(jParser.getText()));
                }
                if ("t12_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.t12Max(converter.parseToDouble(jParser.getText()));
                }
                if ("tcm_min".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMin(converter.parseToDouble(jParser.getText()));
                }
                if ("tcm_max".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.tcmMax(converter.parseToDouble(jParser.getText()));
                }
                if ("PBI".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.pbi(converter.parseToDouble(jParser.getText()));
                }
                if ("ProfFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.profFact(converter.parseToDouble(jParser.getText()));
                }
                if ("WedgeFact".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.wedgeFact(converter.parseToDouble(jParser.getText()));
                }
                if ("SQC_CRIT_MAX".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.sqcCritMax(converter.parseToDouble(jParser.getText()));
                }
                if ("PH_1SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph1sgp(converter.parseToDouble(jParser.getText()));
                }
                if ("PH_12SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph12sgp(jParser.getText());
                }
                if ("PH_23SGP".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.ph23sgp(converter.parseToDouble(jParser.getText()));
                }
                if ("estimate".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.estimate(converter.parseToInteger(jParser.getText()));
                }
                if ("lclThckng".equals(fieldname) && jParser.getCurrentToken() == JsonToken.START_OBJECT) {
                    paramBuilder.lclThckng(getStringFromLclThckngSadim(jParser));
                }
                if ("lot_no".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.lotNo(converter.parseToInteger(jParser.getText()));
                }
                if ("melt_no".equals(fieldname)) {
                    jParser.nextToken();
                    paramBuilder.meltNo(converter.parseToInteger(jParser.getText()));
                }
            }
        } catch (IOException | NumberFormatException ioe) {
            throw new SadimJsonProcessingException("Не удалось обработать json от SADIM: " + ioe.getMessage());
        }
        final var param = paramBuilder.build();

        if (sadimDate != null) {
            log.debug("Сведения SADIM с primeId: [{}] от: [{}]", param.getPrimeId(), sadimDate);
        }

        if (param.getPrimeId() != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }

    private String getStringFromLclThckngSadim(JsonParser jParser) throws IOException {
        List<Double> lclThckng = getArrayFromLclThckngSadim(jParser);

        return lclThckng.stream().map(Objects::toString).collect(Collectors.joining(";"));
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
                            onePare.add(converter.parseToDouble(jParser.getText()));
                        }
                        values.add(onePare);
                    }
                }
            }
        }
        return values.stream().map(value -> value.get(0)).collect(Collectors.toList());
    }

}
