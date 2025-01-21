package com.nlmk.kb.server.controller;

import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc1Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc1Response;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc2Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc2Response;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppRequest;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppResponse;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.config.KbConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для принятия запросов на Аттестацию Единицы Продукции
 */
@Validated
@Tag(name = "Принятие запросов на аттестацию", description = "Контроллер для принятия запросов на Аттестацию Единицы Продукции")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Нарушение требований к объекту. Представлены некорректные параметры, либо какой-то из параметров не представлен вовсе", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
@RequestMapping(path = "/attestation", produces = {MediaType.APPLICATION_JSON_VALUE})
public interface AttestationController {

    @Operation(summary = "Принятие запроса на аттестацию ЦТС", description = "Принятие запроса на Аттестацию Единицы Продукции цеха ЦТС (nlmk.l3.ccm.pts)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аттестация Единицы Продукции успешно пройдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmPtsResponse.class))),
    })
    @PostMapping("/ccm/pts")
    CcmPtsResponse postAttestationCcmPts(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Запрос на аттестацию ЕП ЦГП")
                                         @RequestBody @Valid CcmPtsRequest attRequest);

    @Operation(summary = "Принятие запроса на аттестацию КЦ1", description = "Принятие запроса на Аттестацию Единицы Продукции цеха КЦ1 (nlmk.l3.ccm.kc)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аттестация Единицы Продукции успешно пройдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmKc1Response.class)))
    })
    @PostMapping("/ccm/kc1")
    CcmKc1Response postAttestationCcmKc1(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Запрос на аттестацию ЕП КЦ1")
                                         @RequestBody @Valid CcmKc1Request attRequest);

    @Operation(summary = "Принятие запроса на аттестацию КЦ2", description = "Принятие запроса на Аттестацию Единицы Продукции цеха КЦ2 (nlmk.l3.ccm.kc)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аттестация Единицы Продукции успешно пройдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmKc2Response.class)))
    })
    @PostMapping("/ccm/kc2")
    CcmKc2Response postAttestationCcmKc2(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Запрос на аттестацию ЕП КЦ2")
                                         @RequestBody @Valid CcmKc2Request attRequest);

    @Operation(summary = "Принятие запроса на аттестацию ЦХПП", description = "Принятие запроса на Аттестацию Единицы Продукции цеха ЦХПП (nlmk.l3.ccm.phpp)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Аттестация Единицы Продукции успешно пройдена", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmPhppResponse.class)))
    })
    @PostMapping("/ccm/phpp")
    CcmPhppResponse postAttestationCcmPhpp(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                           @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Запрос на аттестацию ЦХПП")
                                         @RequestBody @Valid CcmPhppRequest attRequest);

}
