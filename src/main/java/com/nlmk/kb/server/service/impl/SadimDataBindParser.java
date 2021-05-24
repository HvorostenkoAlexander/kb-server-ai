package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.SadimJsonParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import nlmk.sadim.Sadim;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Optional;

@Slf4j
@Service("sadimDataBindParser")
public class SadimDataBindParser implements SadimJsonParser {

    public Optional<PreAttestationParam> getParam(String jsonString) {

        Sadim sadim = null;
        val paramBuilder = PreAttestationParam.builder();

        try {
            sadim = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                    .readValue(jsonString, Sadim.class);

            val strip = sadim.getStrips().get(0);

            paramBuilder
                    .primeId(strip.getPrimeId())
                    .pbi(strip.getPbi())
                    .estimate(strip.getAsis().getEstimate())
                    .ph1sgp(strip.getPh1sgp())
                    .ph12sgp(strip.getPh12sgp())
                    .ph23sgp(strip.getPh23sgp())
                    .profFact(strip.getProfFact())
                    .sqcCritMax(strip.getSqcCritMax())
                    .t12Max(strip.getT12Max())
                    .t12Min(strip.getT12Min())
                    .tcmMax(strip.getTcmMax())
                    .tcmMin(strip.getTcmMin())
                    .wedgeFact(strip.getWedgeFact());

            paramBuilder.lclThckng(
                    strip.getLclThckng().getValues().stream().map(
                            d -> d.get(0)
                    ).toArray(Double[]::new)
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Не удалось обработать json от SADIM: " + e.getMessage());
        }
        val param = paramBuilder.build();

        if (param.getPrimeId() != null) {
            return Optional.of(param);
        } else {
            return Optional.empty();
        }
    }
}
