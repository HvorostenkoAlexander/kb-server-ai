package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.service.CcmMessageService;
import com.nlmk.kb.server.service.PreAttestationParamService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
@Timed(percentiles = {0.99, 0.95})
@RestController
@RequiredArgsConstructor
public class KbController {

    private final PreAttestationParamService paramService;
    private final CcmMessageService messageSerivce;

    @GetMapping("/sadim")
    public ResponseEntity<PreAttestationParamDto> sadim(@RequestParam(value = "primeId",
            required = true) String primeId) {

        val paramDto = paramService.findByPrimeIdLatest(primeId);

        return ResponseEntity.ok(paramDto);
    }

    @GetMapping("/attestation_request")
    public Page<CcmAttestationRequestMessage> getAllByPage(@RequestParam(value = "pageNumber", required = true) int page,
                                                           @RequestParam(value = "pageSize", required = true) int size) {
            return messageSerivce.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/attestation_request/{primeId}")
    public List<CcmAttestationRequestMessage> getByPrimeId(@PathVariable String primeId) {

        return messageSerivce.findByPrimeId(primeId);
    }
}
