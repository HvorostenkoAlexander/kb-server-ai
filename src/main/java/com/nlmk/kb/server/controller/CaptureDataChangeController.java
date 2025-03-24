package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для принятия запросов связанных с CaptureDataChanges механизма справочников
 */

@Validated
@Tag(name = "Принятие запросов на обновление статуса MdmMessage", description = "Контроллер для принятия запросов на обновление статуса MdmMessage")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Нарушение требований к объекту. Представлены некорректные параметры, либо какой-то из параметров не представлен вовсе", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
@RequestMapping(path = "/cdc", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface CaptureDataChangeController {

    @Operation(summary = "Обновление статуса MdmMessage", description = "Принятие запроса на обновление статуса MdmMessage", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аттестация Единицы Продукции успешно пройдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/update")
    ResponseEntity<String> updateMdmMessageStatus(@RequestBody String message);

}
