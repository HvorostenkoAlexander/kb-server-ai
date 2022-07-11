package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.AvroVersion;
import com.nlmk.kb.server.service.result_config.AvroVersionService;
import com.nlmk.kb.server.service.result_config.ResultConfigService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(
        origins = "*",
        methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET, RequestMethod.PUT, RequestMethod.DELETE}
)
public class ResultConfigControllerImpl implements ResultConfigController {

    private final ResultConfigService resultConfigService;
    private final AvroVersionService avroVersionService;

    @Override
    public List<AvroVersion> getAvroVersionLists() {
        return avroVersionService.findAll();
    }

    @Override
    public Page<ResultsConfigDto> getResultConfigByPage(int page, int size) {
        log.info("getResultConfigByPage, page: [{}], size: [{}]", page, size);
        return resultConfigService.findPyPage(PageRequest.of(page, size));
    }

    @Override
    public ResultsConfigDto getResultConfigById(Long id) {
        log.info("getResultConfigById, id: [{}]", id);
        return resultConfigService.findById(id);
    }

    @Override
    public ResultsConfigDto postResultConfig(ResultsConfigDto dto) {
        log.info("postResultConfig, dto: [{}]", dto);
        return resultConfigService.create(dto);
    }

    @Override
    public ResultsConfigDto putResultConfig(ResultsConfigDto dto, long id) {
        log.info("putResultConfig, dto: [{}], id: [{}]", dto, id);
        dto.setId(id);
        return resultConfigService.update(dto);
    }

    @Override
    public Long deleteResultConfig(Long id) {
        log.info("deleteResultConfig, id: [{}]", id);
        resultConfigService.deleteById(id);
        return id;
    }

}
