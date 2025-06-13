package com.nlmk.kb.server.api;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import javax.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
@Schema(description = "Параметры запроса для запуска реимпорта MDM")
public class ReimportRequestDto {

    @Schema(description = "ID записи MDM", example = "123")
    @PositiveOrZero
    private Long id;

    @Schema(description = "Название топика Kafka", example = "000-1.l3-nsi-zifra.cdc.sp-dimension.0")
    private String topic;

    @Schema(description = "Статус записи note", example = "DELAYED")
    private String note;

    @Schema(description = "Начало интервала (timestamp with time zone)",
            type = "string", format = "date-time", example = "2024-01-01T00:00:00Z")
    private Instant dstart;

    @Schema(description = "Конец интервала (timestamp with time zone)",
            type = "string", format = "date-time", example = "2024-01-31T23:59:59Z")
    private Instant dend;
}
