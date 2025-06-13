package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.service.zifra.ReimportService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {RequestMethod.POST})
@RestController
@RequiredArgsConstructor
public class ReimportControllerImpl implements ReimportController {
    private final ReimportService reimportService;

    @Override
    public String getReimportStart(ReimportRequestDto requestDto) {
        log.info("getReimportStart");
       return reimportService.startReimport(ReimportType.MDM_MESSAGE, requestDto);
    }

    @Override
    public ReimportDto getReimportStop() {
        log.info("getReimportStop");
        return reimportService.stopReimport(ReimportType.MDM_MESSAGE);
    }
}
