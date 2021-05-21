package com.nlmk.kb.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreAttestationParam {
    @JsonProperty("primeId")
    private String primeId; //"идентификатор"

    @JsonProperty("t12_min")
    private Double t12Min;// "Температура конца прокатки (мin)"

    @JsonProperty("t12_max")
    private Double t12Max;// "Температура конца прокатки (мах)"

    @JsonProperty("tcm_min")
    private Double tcmMin;// "Температура смотки (мin)"

    @JsonProperty("tcm_max")
    private Double tcmMax;// "Температура смотки (мах)"

    @JsonProperty("PBI")
    private Double pbi;// "Процент длины полосы, на которой ширина в допуске"

    @JsonProperty("ProfFact")
    private Double profFact;// "Профиль"

    @JsonProperty("WedgeFact")
    private Double wedgeFact;// "Клин"

    @JsonProperty("SQC_CRIT_MAX")
    private Double sqcCritMax;// "Наибольшая критичность дефекта на полосе"

    @JsonProperty("PH_1SGP")
    private Double ph1sgp;// "Процент длины полосы, на которой толщина входит в полный допуск"

    @JsonProperty("PH_12SGP")
    private Double ph12sgp;// "Процент длины полосы, на которой толщина входит в (1/2) допуска"

    @JsonProperty("PH_23SGP")
    private Double ph23sgp;// "Процент длины полосы, на которой толщина входит в (2/3) допуска"

    @JsonProperty("lclThckng")
    private Double[] lclThckng;// "Высота местных утолщений по ширине полосы"

    @JsonProperty("estimate")
    private Integer estimate;// "Оценка годности полосы"
}
