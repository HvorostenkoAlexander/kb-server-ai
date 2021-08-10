package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.PreAttestationParamDto;
import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.service.CcmMessageService;
import com.nlmk.kb.server.service.PdmMessageService;
import com.nlmk.kb.server.service.PreAttestationParamService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;


@Slf4j
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
@Timed(percentiles = {0.99, 0.95})
@RestController
@RequiredArgsConstructor
public class KbController {

    private final PreAttestationParamService paramService;
    private final CcmMessageService ccmMessageService;
    private final PdmMessageService pdmMessageService;

    @GetMapping("/sadim")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<PreAttestationParamDto> sadim(@RequestParam(value = "primeId") String primeId) {

        log.info("request PreAttestationParamDto for primeId: {}", primeId);

        final var paramDto = paramService.findByPrimeIdLatest(primeId);

        log.info("RESULT paramDto from kb: {}", paramDto);
        return ResponseEntity.ok(paramDto);
    }

    @GetMapping("/attestation_request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Page<CcmAttestationRequestMessage> getAllByPage(@RequestParam(value = "pageNumber", required = true) int page,
                                                           @RequestParam(value = "pageSize", required = true) int size) {
        return ccmMessageService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/attestation_request/{primeId}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public List<CcmAttestationRequestMessage> getByPrimeId(@PathVariable String primeId) {

        return ccmMessageService.findByPrimeId(primeId);
    }

    @GetMapping("/pdm_message")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Page<PdmMessageDto> getPdmTopicMessages(
            @RequestParam(value = "pageNumber", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int size,
            @RequestParam(value = "topic") String topic,
            @RequestParam(value = "posted", required = false) Boolean isPosted,
            @RequestParam(value = "dstart", required = false, defaultValue = "1970-01-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "dend", required = false, defaultValue = "2200-01-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        log.info("kb, getPdmTopicMessages: topic: [{}], posted: [{}], startDate: [{}], endDate: [{}]",
                topic, isPosted, startDate, endDate);

        return pdmMessageService.getMessages(topic,
                isPosted,
                startDate,
                endDate,
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/pdm_message/by_topic")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public List<PdmMessageDto> getPdmTopicMessagesByOffset(@RequestParam(value = "topic") String topic,
                                                         @RequestParam(value = "partition") Integer partition,
                                                         @RequestParam(value = "offset") Long offset){
        log.info("kb, getPdmTopicMessagesByOffset: topic: [{}], partition: [{}], offset: [{}]",
                topic, partition, offset);

        return pdmMessageService.getMessagesByOffset(topic,partition,offset);
    }

    @GetMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public PdmMessageDto getPdmMessageById(@PathVariable Long id){
        log.info("kb, getPdmMessageById: id: [{}]",id);

        return pdmMessageService.getMessageById(id);
    }

    @PostMapping("/pdm_message/{id}/send")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<Long> resendingPdmMessageById(@PathVariable Long id){
        log.info("kb, resendingPdmMessageById: id: [{}]",id);

        final var responseFromNsi = pdmMessageService.resendingToNsi(id);
        return new ResponseEntity<>(id,responseFromNsi.getStatusCode());
    }

    @DeleteMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Long deletePdmMessageById(@PathVariable Long id){
        log.info("kb, deletePdmMessageById: id: [{}]",id);

        return pdmMessageService.deleteMessageById(id);
    }
}
