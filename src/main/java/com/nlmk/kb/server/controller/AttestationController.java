package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.config.KbConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Контроллер для принятия запросов на Аттестацию Единицы Продукции
 */
@Validated
@RequestMapping(path = "/attestation", produces = {MediaType.APPLICATION_JSON_VALUE})
@ApiResponse(responseCode = "400",
        description = "Нарушение требований к объекту", content = @Content)
@ApiResponse(responseCode = "401",
        description = "Требуется пройти авторизацию, нужен JWT", content = @Content)
@ApiResponse(responseCode = "403",
        description = "Доступ к ресурсу ограничен, нет прав у роли, указанной в JWT", content = @Content)
public interface AttestationController {

    @PostMapping("/ccm/pts")
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(summary = "Запрос на Аттестацию Единицы Продукции, цех ЦТС (nlmk.l3.ccm.pts)",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "201",
            description = "Аттестация Единицы Продукции успешно пройдена", content = @Content)
    CcmPtsResponse postAttestationCcmPts(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @RequestBody @Valid CcmPtsRequest attRequest);

}
