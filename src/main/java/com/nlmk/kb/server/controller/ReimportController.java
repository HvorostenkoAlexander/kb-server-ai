package com.nlmk.kb.server.controller;


import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Tag(name = "Reimport Operations", description = "API для действий связанных с функционалом реимпорта")
@ApiResponses({
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
@RequestMapping(value = "/reimport", produces = MediaType.APPLICATION_JSON_VALUE)
public interface ReimportController {

    @Operation(summary = "Запуск реимпорта MDM",
            description = "Фильтрация по ID, топику, статусу и временному интервалу.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Реимпорт записей mdm_message завершён", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/start")
    String getReimportStart(@RequestBody @Valid ReimportRequestDto request);

    @Operation(summary = "Остановка процесса реимпорта",
            description = "Останавливается процесс реимпорта даже если он не был запущен",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Процесс реимпорта успешно остановлен", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/stop")
    ReimportDto getReimportStop();
}

