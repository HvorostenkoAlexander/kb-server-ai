package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.DictionaryConfigDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер конфигурации
 */
@Validated
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public interface DictionaryConfigController {

    @GetMapping("/configuration")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Page<DictionaryConfigDto> getDtosByPage(@RequestParam(value = "pageNumber", defaultValue = "0") int page,
                                            @RequestParam(value = "pageSize", defaultValue = "20") int size);

    @GetMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    DictionaryConfigDto getDictionaryConfigDtoById(@PathVariable Long id);

    @PutMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    DictionaryConfigDto updateDictionaryConfigDto(@PathVariable Long id,
                                                  @RequestBody @Valid DictionaryConfigDto dto);

    @DeleteMapping("/configuration/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Long deleteDictionaryConfigDtoById(@PathVariable Long id);
}
