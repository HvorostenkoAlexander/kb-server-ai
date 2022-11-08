package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Validated
@ApiResponse(responseCode = "400",
        description = "Нарушение требований к объекту", content = @Content)
@ApiResponse(responseCode = "401",
        description = "Требуется пройти авторизацию, нужен JWT", content = @Content)
@ApiResponse(responseCode = "403",
        description = "Доступ к ресурсу ограничен, нет прав у роли, указанной в JWT", content = @Content)
public interface KbController {

    @PostMapping("/launch_attestation/{primeId}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    ResponseEntity<String> postLaunchReAttestation(@PathVariable String primeId);

    @GetMapping("/attestation_request")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Page<CcmMessage> getAttestationRequestAllByPage(@RequestParam(value = "pageNumber") int page,
                                                    @RequestParam(value = "pageSize") int size);

    @GetMapping("/attestation_request/{primeId}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @Deprecated(since = "1.33.0")
    List<CcmMessage> getCcmMessageByPrimeId(@PathVariable String primeId);

    @GetMapping("/attestation/request/{primeId}")
    @Operation(summary = "Получения последнего запросов на Аттестацию для указанного primeId",
            security = {@SecurityRequirement(name = "bearer-key")})
    AttestationRequest getAttestationRequestForPrimeId(@PathVariable String primeId);

    @GetMapping("/pdm_message")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Page<PdmMessageDto> getPdmTopicMessages(
            @RequestParam(value = "pageNumber", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "20") int size,
            @RequestParam(value = "topic") String topic,
            @RequestParam(value = "posted", required = false) Boolean isPosted,
            @RequestParam(value = "dstart", required = false, defaultValue = "1970-01-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(value = "dend", required = false, defaultValue = "2200-01-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

    @GetMapping("/pdm_message/by_topic")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    List<PdmMessageDto> getPdmTopicMessagesByOffset(@RequestParam(value = "topic") String topic,
                                                    @RequestParam(value = "partition") Integer partition,
                                                    @RequestParam(value = "offset") Long offset);

    @GetMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    PdmMessageDto getPdmMessageById(@PathVariable Long id);

    @PostMapping("/pdm_message/{id}/send")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    ResponseEntity<Long> postResendingPdmMessageById(@PathVariable Long id);

    @DeleteMapping("/pdm_message/{id}")
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    Long deletePdmMessageById(@PathVariable Long id);

    @PostMapping("/sap_message/zorder")
    @Operation(summary = "Парсинг и сохранение заказа, переданного в виде xml",
            security = {@SecurityRequirement(name = "bearer-key")})
    ResponseEntity<String> postSendingSapMessage(
            @RequestBody @Schema(example = "<?xml version=... ?><ZORDERS05_1></ZORDERS05_1>") String message
    ) throws JsonProcessingException;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send_attestation_result")
    @Operation(summary = "Передача результата Аттестации в ССМ",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200",
            description = "Результат Аттестации успешно передан в ССМ", content = @Content)
    @ApiResponse(responseCode = "500",
            description = "Ошибки подготовки сообщения к отправке", content = @Content)
    @ApiResponse(responseCode = "502",
            description = "Отправка сообщения закончилась ошибкой", content = @Content)
    @ApiResponse(responseCode = "503",
            description = "Ошибки настройки сервиса отправки сообщений", content = @Content)
    void postProductAttestationResult(@RequestBody @Valid ProductAttestationResultDto attestationResult);

    @GetMapping("/ccm_source_message")
    @Operation(summary = "Поиск исходного сообщения запроса аттестации ССМ по id запроса",
            security = {@SecurityRequirement(name = "bearer-key")})
    ResponseEntity<String> getCcmSourceMessage(@RequestParam(value = "requestId") Long requestId);
}
