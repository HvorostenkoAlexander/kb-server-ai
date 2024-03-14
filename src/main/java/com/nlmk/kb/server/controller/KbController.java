package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.product.api.kb.SapMessageDto;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.product.api.swagger.ErrorResponseDto;
import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.api.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmMessage;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Validated
@Tag(name = "Принятие запросов на аттестацию", description = "Контроллер для принятия запросов на Аттестацию Единицы Продукции")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Нарушение требований к объекту. Представлены некорректные параметры, либо какой-то из параметров не представлен вовсе", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Не выполнена аутентификация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
        @ApiResponse(responseCode = "403", description = "Не пройдена авторизация", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
})
public interface KbController {

    @Operation(summary = "Запуск повторной аттестации", description = "Запуск повторной аттестации по запросу на аттестацию с primeId", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Повторный запрос на аттестацию успешно отправлен", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/launch_attestation/{primeId}")
    ResponseEntity<String> postLaunchReAttestation(@Parameter(description = "Идентификатор Единицы Металла") @PathVariable String primeId);

    @Operation(summary = "Получение страницы запросов на аттестацию", description = "Получение страницы запросов на аттестацию", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Страница запросов на аттестацию получена успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = CcmMessage.class)))),
    })
    @GetMapping("/attestation_request")
    Page<CcmMessage> getAttestationRequestAllByPage(@Parameter(description = "Номер страницы пагинации") @RequestParam(value = "pageNumber") int page,
                                                    @Parameter(description = "Размер страницы пагинации") @RequestParam(value = "pageSize") int size);

    @Operation(summary = "Получение страницы сообщений из топиков PDM-справочников", description = "Получение страницы сообщений из топиков PDM-справочников", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения получены успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PdmMessageDto.class)))),
    })
    @GetMapping("/pdm_message")
    Page<PdmMessageDto> getPdmTopicMessages(
            @Parameter(description = "Номер страницы пагинации") @RequestParam(value = "pageNumber", defaultValue = "0") int page,
            @Parameter(description = "Размер страницы пагинации") @RequestParam(value = "pageSize", defaultValue = "20") int size,
            @Parameter(description = "Название топика") @RequestParam(value = "topic") String topic,
            @Parameter(description = "Статус отправки в НСИ (true/false)") @RequestParam(value = "posted", required = false) Boolean isPosted,
            @Parameter(description = "Фильтр по периоду. Старт интервала") @RequestParam(value = "dstart", required = false, defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @Parameter(description = "Фильтр по периоду. Конец интервала") @RequestParam(value = "dend", required = false, defaultValue = "2200-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate);

    @Operation(summary = "Получение сообщений из топиков PDM-справочников по данным Kafka", description = "Получение сообщений из топиков PDM-справочников по данным Kafka", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщения получены успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = PdmMessageDto.class)))),
    })
    @GetMapping("/pdm_message/by_topic")
    List<PdmMessageDto> getPdmTopicMessagesByOffset(@Parameter(description = "Название топика") @RequestParam(value = "topic") String topic,
                                                    @Parameter(description = "Название партиции") @RequestParam(value = "partition") Integer partition,
                                                    @Parameter(description = "Смещение в партиции топика") @RequestParam(value = "offset") Long offset);

    @Operation(summary = "Получение сообщения из PDM-справочника по id", description = "Получение сообщения сообщений PDM-справочников по id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение получено успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PdmMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Сообщение с указанным id не найдено", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/pdm_message/{id}")
    PdmMessageDto getPdmMessageById(@Parameter(description = "id сообщения из PDM") @PathVariable Long id);

    @Operation(summary = "Отправка запроса на переотправку сообщения из PDM", description = "Отправка запроса на переотправку сообщения из PDM", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Запрос на переотправку сообщения отправлен успешно", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "404", description = "Сообщение с указанным id не найдено", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @PostMapping("/pdm_message/{id}/send")
    ResponseEntity<Long> postResendingPdmMessageById(@Parameter(description = "") @PathVariable Long id);

    @Operation(summary = "Удаление сообщения PDM-справочников по id", description = "Удаление сообщения PDM-справочников по id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение удалено успешно", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    @DeleteMapping("/pdm_message/{id}")
    Long deletePdmMessageById(@Parameter(description = "id сообщения для удаления") @PathVariable Long id);

    @Operation(summary = "Сохранение заказа ZORDER", description = "Парсинг и сохранение заказа, переданного в виде xml", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обработан, распарсен и передан в psm", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/sap_message/zorder")
    ResponseEntity<String> postSendingSapZorderMessage(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "xml представление объекта ZORDERS05_1 заказа SAP", content = @Content(schema = @Schema(example = "<?xml version=... ?><ZORDERS05_1></ZORDERS05_1>")))
                                                       @RequestBody String message) throws JsonProcessingException;

    @Operation(summary = "Сохранение заказа ZMMORDER", description = "Парсинг и сохранение заказа, переданного в виде xml", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обработан, распарсен и передан в psm", content = @Content(schema = @Schema(implementation = String.class))),
    })
    @PostMapping("/sap_message/zmmorder")
    ResponseEntity<String> postSendingSapZmmorderMessage(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "xml представление объекта ZMMORDERS05_DOP заказа SAP")
                                                         @RequestBody @Schema(example = "<?xml version=... ?><ZMMORDERS05_DOP></ZMMORDERS05_DOP>") String message) throws JsonProcessingException;

    @Operation(summary = "Запрос получения сообщения SAP, следующего после указанного id", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SapMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Сообщение не найдено", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    })
    @GetMapping("/sap_message/next")
    SapMessageDto<?> getSapMessageNextId(@Parameter(description = "id сообщения SAP с которого производится поиск следующего сообщения") @RequestParam(value = "id", required = false) Long id);

    @Operation(summary = "Передача результата Аттестации в ССМ", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Результат Аттестации успешно передан в ССМ", content = @Content),
            @ApiResponse(responseCode = "500", description = "Ошибки подготовки сообщения к отправке", content = @Content),
            @ApiResponse(responseCode = "502", description = "Отправка сообщения закончилась ошибкой", content = @Content),
            @ApiResponse(responseCode = "503", description = "Ошибки настройки сервиса отправки сообщений", content = @Content),
    })
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/send_attestation_result")
    void postProductAttestationResult(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Ответ pam-server - результат Аттестации Единицы Продукции")
                                      @RequestBody @Valid ProductAttestationResultDto attestationResult);

    @Operation(summary = "Получения последнего запросов на Аттестацию для указанного primeId (первоисточник запроса)", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmMessageSourceDto.class))),
    })
    @GetMapping("/attestation/request/{primeId}")
    ResponseEntity<CcmMessageSourceDto> getSourceRequestByPrimeId(@Parameter(description = "Идентификатор Единицы Металла") @PathVariable String primeId);

    @Operation(summary = "Поиск исходного сообщения ССМ с запросом на Аттестацию по id Запроса Аттестации", security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Сообщение найдено", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CcmMessageSourceDto.class))),
    })
    @GetMapping("/ccm_source_message")
    ResponseEntity<CcmMessageSourceDto> getSourceRequestByRequestId(@Parameter(description = "id запроса на аттестацию") @RequestParam(value = "requestId") Long requestId);

}
