package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.AvroVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RequestMapping(path = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
@ApiResponse(responseCode = "400",
        description = "Нарушение требований к объекту", content = @Content)
@ApiResponse(responseCode = "401",
        description = "Требуется пройти авторизацию, нужен JWT", content = @Content)
@ApiResponse(responseCode = "403",
        description = "Доступ к ресурсу ограничен, нет прав у роли, указанной в JWT", content = @Content)
public interface ResultConfigController {
    @GetMapping("/avro")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    List<AvroVersion> getAvroVersionLists();

    @GetMapping("/topics")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Page<ResultsConfigDto> getResultConfigByPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int size);

    @GetMapping("/topics/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    ResultsConfigDto getResultConfigById(@PathVariable Long id);

    @PostMapping("/topics")
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    ResultsConfigDto postResultConfig(@RequestBody @Valid ResultsConfigDto dto);

    @PutMapping("/topics/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    ResultsConfigDto putResultConfig(@RequestBody @Valid ResultsConfigDto dto,
                                     @PathVariable long id);

    @DeleteMapping("/topics/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Long deleteResultConfig(@PathVariable Long id);

}
