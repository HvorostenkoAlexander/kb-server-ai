package com.nlmk.kb.server.api.ccm.kc.request;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@Jacksonized
public class ChemData {

    private final Long sampleId; // ИД пробы
    private final @NotBlank String probeCode; // Вид пробы ( C-сталь )
    private final @NotBlank String analysisCode; // Тип анализа (М-маркировочный. С-сляб, K-контрольный)
    private final Integer sampleNum; // Номер пробы (образца)
    private final @NotNull Integer heat; //  Номер плавки
    private final String samplingPlaceName; // Место отбора (Слябный К-концевая часть сляба, Н-головная часть сляба)
    private final String reason; // Причина отрезки пробы
    private final @NotEmpty List<@Valid Chemical> chemical; // Значения химического анализа

}
