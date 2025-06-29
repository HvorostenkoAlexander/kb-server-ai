package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.service.zifra.ReimportService;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {RequestMethod.POST})
@RestController
public class PdmReimportControllerImpl implements PdmReimportController {


    private final ReimportService reimportService;

    @Autowired
    public PdmReimportControllerImpl(@Qualifier("PdmReimportServiceImpl") ReimportService reimportService) {
        this.reimportService = reimportService;
    }

    @Override
    public String postReimportStart(ReimportRequestDto requestDto) {
        log.info("getReimportStart");
        return reimportService.startReimport(ReimportType.PDM_MESSAGE, requestDto);
    }

    @Override
    public ReimportDto getReimportStop() {
        log.info("getReimportStop");
        return reimportService.stopReimport(ReimportType.PDM_MESSAGE);
    }
}