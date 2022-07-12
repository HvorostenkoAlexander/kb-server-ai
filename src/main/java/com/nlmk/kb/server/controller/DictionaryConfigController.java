package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import com.nlmk.kb.server.service.pdm.DictionaryConfigService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DictionaryConfigController {

    private final DictionaryConfigService configService;

    @GetMapping("/configuration")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Page<DictionaryConfigDto> getDtosByPage(@RequestParam(value = "pageNumber", defaultValue = "0") int page,
                                                   @RequestParam(value = "pageSize", defaultValue = "20") int size) {
        log.info("kb, getDtosByPage: pageNumber: [{}], pageSize:  [{}]", page, size);
        return configService.findPyPage(PageRequest.of(page, size));
    }

    @GetMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public DictionaryConfigDto getDictionaryConfigDtoById(@PathVariable Long id) {
        log.info("kb, getDictionaryConfigDtoById: id: [{}]", id);

        return configService.findById(id);
    }

    @PutMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public DictionaryConfigDto updateDictionaryConfigDto(@PathVariable Long id,
                                                         @RequestBody @Valid DictionaryConfigDto dto) {
        log.info("kb, updateDictionaryConfigDto, by id [{}], [{}]", id, dto);

        return configService.update(id, dto);
    }

    @DeleteMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    public Long deleteDictionaryConfigDtoById(@PathVariable Long id) {
        log.info("kb, deleteDictionaryConfigDtoById: id: [{}]", id);

        configService.deleteById(id);
        return id;
    }
}
