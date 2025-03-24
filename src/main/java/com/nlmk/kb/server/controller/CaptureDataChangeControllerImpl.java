package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.kb.CdcUpdate;
import com.nlmk.kb.server.service.CaptureDataChangeService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST})
public class CaptureDataChangeControllerImpl implements CaptureDataChangeController {

    private final CaptureDataChangeService captureDataChangeService;
    @Override
    public ResponseEntity<String> updateMdmMessageStatus(CdcUpdate cdcUpdate) {
        log.info("Обновление записи CDC: [{}]", cdcUpdate.toString());
        captureDataChangeService.updateMdmMessage(cdcUpdate);
        return new ResponseEntity<>(cdcUpdate.getMessageId(), HttpStatus.OK);
    }
}
