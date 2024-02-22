package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import com.nlmk.kb.server.api.DictionaryConfigDto;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Конфигурация справочников PDM", description = "Контроллер конфигурации справочников PDM")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
@RequestMapping(path = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
public interface DictionaryConfigController {

    @Operation(summary = "Получение страницы конфигов справочников PDM", description = "Получение страницы конфигов справочников PDM", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиги справочников PDM получены успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CcmPtsResponse.class)))),
    })
    @GetMapping
    Page<DictionaryConfigDto> getDtosByPage(@Parameter(description = "Номер страницы пагинации") @RequestParam(value = "pageNumber", defaultValue = "0") int page,
                                            @Parameter(description = "Размер страницы пагинации") @RequestParam(value = "pageSize", defaultValue = "20") int size);

    @Operation(summary = "Получение конфига справочника PDM по id", description = "Получение конфига справочника PDM по id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг справочника PDM получен успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DictionaryConfigDto.class))),
            @ApiResponse(responseCode = "404", description = "Конфиг с указанным id не найден", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/{id}")
    DictionaryConfigDto getDictionaryConfigDtoById(@Parameter(description = "id конфига для получения") @PathVariable Long id);

    @Operation(summary = "Замена конфига справочника PDM по id", description = "Замена (upsert) конфига справочника PDM по id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг справочника PDM заменен успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DictionaryConfigDto.class))),
            @ApiResponse(responseCode = "404", description = "Конфиг с указанным id не найден", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PutMapping("/{id}")
    DictionaryConfigDto updateDictionaryConfigDto(@Parameter(description = "id конфига для обновления") @PathVariable Long id,
                                                  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Измененный конфиг для справочника")
                                                  @RequestBody @Valid DictionaryConfigDto dto);

    @Operation(summary = "Удаление конфига справочника PDM по id", description = "Удаление конфига справочника PDM по id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг справочника PDM заменен успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DictionaryConfigDto.class))),
            @ApiResponse(responseCode = "404", description = "Конфиг с указанным id не найден", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @DeleteMapping("/{id}")
    Long deleteDictionaryConfigDtoById(@Parameter(description = "id конфига для удаления") @PathVariable Long id);
}
