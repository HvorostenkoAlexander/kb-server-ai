package com.nlmk.kb.server.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.SadimJsonParser;
import lombok.val;

import nlmk.l3.pdm.SpMicrostructure;
import nlmk.sadim.LclThckng;
import nlmk.sadim.Sadim;
import org.apache.kafka.common.quota.ClientQuotaAlteration;

import org.springframework.stereotype.Service;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SadimJsonParserImpl implements SadimJsonParser {

    public Optional<PreAttestationParam> getParam(String jsonString) {

        Sadim sadim = null;

        try {
            sadim = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))
                    .readValue(jsonString, Sadim.class);

            val paramBuilder = PreAttestationParam.builder();
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
                    strip.getLclThckng().getValues().stream().map(d->d.get(0)).toArray(Double[]::new)
            );

            val param = paramBuilder.build();
            System.out.println("--- param: "+param);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

      //  System.out.println("---sadim: "+sadim);
        return Optional.empty();
    }

//
//    @Override
//    public Optional<PreAttestationParam> getParam(String jsonString) {
//
//        val paramBuilder = PreAttestationParam.builder();
//
//        try (JsonParser jParser = new JsonFactory().createParser(jsonString);) {
//
//            while (jParser.nextToken() != null) {
//                //String fieldname = jParser.getCurrentName();
//
//             //   if ("strips".equals(jParser.getCurrentName())) {
//                    //  if (jParser.nextToken() == JsonToken.START_ARRAY) {
//                    //    while (jParser.nextToken() != JsonToken.END_ARRAY) { // todo выяснить что ести много членов в массиве?
//                    //    while (jParser.nextToken() != JsonToken.END_OBJECT) {
//
//              //      while (jParser.nextToken() != null) {
//
//                        String fieldname = jParser.getCurrentName();
//                        //System.out.println(" fieldname: "+fieldname);
//                        if ("PRIME_ID".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- PRIME_ID: " + jParser.getText());
//                            paramBuilder.primeId(jParser.getText());
//                        }
//                        if ("t12_min".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- t12_min: " + jParser.getText());
//                            paramBuilder.t12Min(jParser.getDoubleValue());
//                        }
//                        if ("t12_max".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- t12_max: " + jParser.getText());
//                            paramBuilder.t12Max(jParser.getDoubleValue());
//                        }
//                        if ("tcm_min".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- tcm_min: " + jParser.getText());
//                            paramBuilder.tcmMin(jParser.getDoubleValue());
//                        }
//                        if ("tcm_max".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- tcm_max: " + jParser.getText());
//                            paramBuilder.tcmMax(jParser.getDoubleValue());
//                        }
//                        if ("PBI".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- PBI: " + jParser.getText());
//                            paramBuilder.pbi(jParser.getDoubleValue());
//                        }
//                        if ("ProfFact".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- ProfFact: " + jParser.getText());
//                            paramBuilder.profFact(jParser.getDoubleValue());
//                        }
//                        if ("WedgeFact".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- WedgeFact: " + jParser.getText());
//                            paramBuilder.wedgeFact(jParser.getDoubleValue());
//                        }
//                        if ("SQC_CRIT_MAX".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- SQC_CRIT_MAX: " + jParser.getText());
//                            paramBuilder.sqcCritMax(jParser.getDoubleValue());
//                        }
//                        if ("PH_1SGP".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- PH_1SGP: " + jParser.getText());
//                            paramBuilder.ph1sgp(jParser.getDoubleValue());
//                        }
//                        if ("PH_12SGP".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- PH_12SGP: " + jParser.getText());
//                            paramBuilder.ph12sgp(jParser.getDoubleValue());
//                        }
//                        if ("PH_23SGP".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- PH_23SGP: " + jParser.getText());
//                            paramBuilder.ph23sgp(jParser.getDoubleValue());
//                        }
//
//                        if ("estimate".equals(fieldname)) {
//                            jParser.nextToken();
//                            System.out.println("--- estimate: " + jParser.getText());
//                            paramBuilder.estimate(jParser.getIntValue());
//                        }
//
//                        if ("lclThckng".equals(fieldname)) {
//                            jParser.nextToken();
//
//                            System.out.println("--- lclThckng1: "  +jParser.currentName()+"; "+jParser.getText());
//                        }
//                       // System.out.println("--- "+jParser.currentName()+"; "+jParser.getText());
//             //       }
//             //   }
//            }
//        } catch (IOException ioe) {// todo обработать exception
//            System.out.println("уппс: " + ioe);
//        }
//
//
//        return Optional.empty();
//    }


}
