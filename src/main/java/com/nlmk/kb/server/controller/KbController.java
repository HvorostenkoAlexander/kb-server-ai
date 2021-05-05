package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.kb.server.entity.PreAttestationParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KbController {

    @GetMapping("/sadim/{primeId}")
    public ResponseEntity<PreAttestationParam> sadimStub(@PathVariable String primeId) throws JsonProcessingException {

        val stubJson="{\n" +
                "\"primeID\": \"0\",\n" +
                "\"t12_min\": 865,\n" +
                "\"t12_max\": 905,\n" +
                "\"tcm_min\": 550,\n" +
                "\"tcm_max\": 590,\n" +
                "\"PBI\": 100,\n" +
                "\"ProfFact\": 6,\n" +
                "\"WedgeFact\": -10,\n" +
                "\"SQC_CRIT_MAX\": 0,\n" +
                "\"PH_1SGP\": 100,\n" +
                "\"PH_12SGP\": 98,\n" +
                "\"PH_23SGP\": 99,\n" +
                "\"lclThckng\": [],\n" +
                "\"estimate\": 5\n" +
                "}";
        val sadimStub = new ObjectMapper()
                .readValue(stubJson, PreAttestationParam.class);

        sadimStub.setPrimeId(primeId);

        return  ResponseEntity.ok(sadimStub);
    }
}
