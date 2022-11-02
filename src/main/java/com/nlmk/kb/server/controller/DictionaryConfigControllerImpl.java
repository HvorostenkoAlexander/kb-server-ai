package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {
        RequestMethod.OPTIONS,
        RequestMethod.POST,
        RequestMethod.GET,
        RequestMethod.PUT,
        RequestMethod.DELETE})
@RestController
@RequiredArgsConstructor
public class DictionaryConfigControllerImpl implements DictionaryConfigController {

    private final DictionaryConfigService configService;

    @Override
    public Page<DictionaryConfigDto> getDtosByPage(int page, int size) {
        log.info("kb, getDtosByPage: pageNumber: [{}], pageSize:  [{}]", page, size);
        return configService.findPyPage(PageRequest.of(page, size));
    }

    @Override
    public DictionaryConfigDto getDictionaryConfigDtoById(Long id) {
        log.info("kb, getDictionaryConfigDtoById: id: [{}]", id);
        return configService.findById(id);
    }

    @Override
    public DictionaryConfigDto updateDictionaryConfigDto(Long id, DictionaryConfigDto dto) {
        log.info("kb, updateDictionaryConfigDto, by id [{}], [{}]", id, dto);
        return configService.update(id, dto);
    }

    @Override
    public Long deleteDictionaryConfigDtoById(Long id) {
        log.info("kb, deleteDictionaryConfigDtoById: id: [{}]", id);
        configService.deleteById(id);
        return id;
    }
}
