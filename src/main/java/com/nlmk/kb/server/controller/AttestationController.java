package com.nlmk.kb.server.controller;

import com.nlmk.kb.server.api.ccm.kc.request.CcmKc1Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc1Response;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc2Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc2Response;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.config.KbConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
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
@RequestMapping(path = "/attestation", produces = {MediaType.APPLICATION_JSON_VALUE})
@ApiResponse(responseCode = "400",
        description = "Нарушение требований к объекту", content = @Content)
@ApiResponse(responseCode = "401",
        description = "Требуется пройти авторизацию, нужен JWT", content = @Content)
@ApiResponse(responseCode = "403",
        description = "Доступ к ресурсу ограничен, нет прав у роли, указанной в JWT", content = @Content)
public interface AttestationController {

    @PostMapping("/ccm/pts")
    @Operation(summary = "Запрос на Аттестацию Единицы Продукции, цех ЦТС (nlmk.l3.ccm.pts)",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200",
            description = "Аттестация Единицы Продукции успешно пройдена", content = @Content)
    CcmPtsResponse postAttestationCcmPts(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @RequestBody @Valid CcmPtsRequest attRequest);

    @PostMapping("/ccm/kc1")
    @Operation(summary = "Запрос на Аттестацию Единицы Продукции, цех КЦ1 (nlmk.l3.ccm.kc)",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200",
            description = "Аттестация Единицы Продукции успешно пройдена", content = @Content)
    CcmKc1Response postAttestationCcmKc1(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @RequestBody @Valid CcmKc1Request attRequest);

    @PostMapping("/ccm/kc2")
    @Operation(summary = "Запрос на Аттестацию Единицы Продукции, цех КЦ2 (nlmk.l3.ccm.kc)",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200",
            description = "Аттестация Единицы Продукции успешно пройдена", content = @Content)
    CcmKc2Response postAttestationCcmKc2(@RequestHeader(name = KbConstants.REQUEST_ID_HEADER, required = false) String requestId,
                                         @RequestBody @Valid CcmKc2Request attRequest);

}
