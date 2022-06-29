package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.dto.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.*;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import com.nlmk.kb.server.service.sap.S3Service;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
@Timed(percentiles = {0.99, 0.95})
@RestController
@RequiredArgsConstructor
public class KbController {

    private final CcmMessageService ccmMessageService;
    private final PdmMessageService pdmMessageService;
    private final CcmCommonService ccmCommonService;
    private final S3Service s3Service;
    private final PsmSender psmSender;

    @PostMapping("/launch_attestation/{primeId}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<String> launchReAttestation(@PathVariable String primeId) {
        log.info("Повторная отправка запроса на аттестацию из kb-server. primeId:[{}]", primeId);

        final var resultId = ccmCommonService.rePostAttestation(primeId);
        final var resultString = String.format(
                "Результат: [%s] получен при повторной отправки запроса на аттестацию с primeId: [%s]",
                resultId,
                primeId
        );

        return ResponseEntity.ok(resultString);
    }

    @GetMapping("/attestation_request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Page<CcmMessage> getAllByPage(@RequestParam(value = "pageNumber") int page,
                                         @RequestParam(value = "pageSize") int size) {
        return ccmMessageService.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/attestation_request/{primeId}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public List<CcmMessage> getByPrimeId(@PathVariable String primeId) {

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
                                                           @RequestParam(value = "offset") Long offset) {
        log.info("kb, getPdmTopicMessagesByOffset: topic: [{}], partition: [{}], offset: [{}]",
                topic, partition, offset);

        return pdmMessageService.getMessagesByOffset(topic, partition, offset);
    }

    @GetMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public PdmMessageDto getPdmMessageById(@PathVariable Long id) {
        log.info("kb, getPdmMessageById: id: [{}]", id);

        return pdmMessageService.getMessageById(id);
    }

    @PostMapping("/pdm_message/{id}/send")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<Long> resendingPdmMessageById(@PathVariable Long id) {
        log.info("kb, resendingPdmMessageById: id: [{}]", id);

        final var responseFromNsi = pdmMessageService.resendingToNsi(id);
        return new ResponseEntity<>(id, responseFromNsi.getStatusCode());
    }

    @DeleteMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Long deletePdmMessageById(@PathVariable Long id) {
        log.info("kb, deletePdmMessageById: id: [{}]", id);

        return pdmMessageService.deleteMessageById(id);
    }

    @PostMapping("/sap_message/zorder")
    @Operation(summary = "Парсинг и сохранение заказа, переданного в виде xml",
            security = {@SecurityRequirement(name = "bearer-key")})
    public ResponseEntity<String> sendingSapMessage(
            @RequestBody @Schema(example = "<?xml version=... ?><ZORDERS05_1></ZORDERS05_1>") String message
    ) throws JsonProcessingException {
        log.info("kb, sendingSapMessage: message: [{}]", message);

        ZORDERS051 zorder = s3Service.getZorder(message);

        psmSender.postZorder(zorder);
        log.info("kb, sendingSapMessage. Sent to PSM BELNR: [{}]", zorder.getIDOC().getE1EDK01().getBELNR());

        return new ResponseEntity<>("BELNR: " + zorder.getIDOC().getE1EDK01().getBELNR(), HttpStatus.OK);
    }
}
