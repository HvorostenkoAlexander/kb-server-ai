package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import com.nlmk.kb.server.api.ResultsConfigDto;
import com.nlmk.kb.server.entity.AvroVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.List;

@Validated
@Tag(name = "Конфигурация топиков", description = "Контроллер конфигурации топиков")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Нарушение требований к объекту. Представлены некорректные параметры, либо какой-то из параметров не представлен вовсе", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
@RequestMapping(path = "/configuration", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ResultConfigController {

    @Operation(summary = "Получение версий схем AVRO", description = "Получение версий схем AVRO", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Версии схем AVRO успешно получены", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = AvroVersion.class)))),
    })
    @GetMapping("/avro")
    List<AvroVersion> getAvroVersionLists();

    @Operation(summary = "Получение конфигов топиков и их AVRO схем", description = "Поиск конфигурации по наименованию головного объекта AVRO схемы", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиги AVRO успешно получены", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = ResultsConfigDto.class)))),
    })
    @GetMapping("/topics")
    Page<ResultsConfigDto> getResultConfigByPage(@Parameter(description = "Номер страницы пагинации") @RequestParam(value = "page", defaultValue = "0") int page,
                                                 @Parameter(description = "Размер страницы пагинации") @RequestParam(value = "pageSize", defaultValue = "10") int size);

    @Operation(summary = "Получение конфига топика и его AVRO схемы", description = "Поиск конфигурации по наименованию головного объекта AVRO схемы", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг AVRO успешно получен", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultsConfigDto.class))),
            @ApiResponse(responseCode = "404", description = "Конфиг с указанным id не найден", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/topics/{id}")
    ResultsConfigDto getResultConfigById(@Parameter(description = "id конфига для получения") @PathVariable Integer id);

    @Operation(summary = "Добавление конфига топика и его AVRO схемы", description = "Добавление конфига топика и его AVRO схемы", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Конфиг AVRO успешно сохранен", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultsConfigDto.class))),
    })
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("/topics")
    ResultsConfigDto postResultConfig(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Конфиг топика и AVRO схемы для сохранения")
                                      @RequestBody @Valid ResultsConfigDto dto);

    @Operation(summary = "Изменение конфига топика и его AVRO схемы", description = "Изменение (upsert) конфига топика и его AVRO схемы", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг AVRO успешно изменен", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResultsConfigDto.class))),
    })
    @PutMapping("/topics/{id}")
    ResultsConfigDto putResultConfig(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Обновленный конфиг топика и AVRO схемы")
                                     @RequestBody @Valid ResultsConfigDto dto,
                                     @Parameter(description = "id конфига для изменения") @PathVariable Integer id);

    @Operation(summary = "Удаление конфига топика и его AVRO схемы", description = "Удаление конфига топика и его AVRO схемы", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Конфиг AVRO успешно удален", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Integer.class))),
    })
    @DeleteMapping("/topics/{id}")
    Integer deleteResultConfig(@Parameter(description = "id конфига для удаления") @PathVariable Integer id);

}
