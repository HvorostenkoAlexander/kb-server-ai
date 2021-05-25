package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.PreAttestationParam;
import com.nlmk.kb.server.service.PreAttestationParamService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Timed(percentiles = {0.99, 0.95})
@RestController
@RequiredArgsConstructor
public class KbController {

    private final PreAttestationParamService service;

    @GetMapping("/sadim")
    public ResponseEntity<List<PreAttestationParamDto>> sadim(@RequestParam(value = "primeId", required = true) String primeId) {

        val sadimData = service.findByPrimeId(primeId);

        return ResponseEntity.ok(sadimData);
    }
}
