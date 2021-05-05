package com.nlmk.kb.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PreAttestationParam {
    @JsonProperty("primeID")
    private String primeID; //"идентификатор"

    @JsonProperty("t12_min")
    private Long t12Min;// "Температура конца прокатки (мin)"

    @JsonProperty("t12_max")
    private Long t12Max;// "Температура конца прокатки (мах)"

    @JsonProperty("tcm_min")
    private Long tcmMin;// "Температура смотки (мin)"

    @JsonProperty("tcm_max")
    private Long tcmMax;// "Температура смотки (мах)"

    @JsonProperty("PBI")
    private Long pbi;// "Процент длины полосы, на которой ширина в допуске"

    @JsonProperty("ProfFact")
    private Long profFact;// "Профиль"

    @JsonProperty("WedgeFact")
    private Long wedgeFact;// "Клин"

    @JsonProperty("SQC_CRIT_MAX")
    private Long sqcCritMax;// "Наибольшая критичность дефекта на полосе"

    @JsonProperty("PH_1SGP")
    private Long ph1sgp;// "Процент длины полосы, на которой толщина входит в полный допуск"

    @JsonProperty("PH_12SGP")
    private Long ph12sgp;// "Процент длины полосы, на которой толщина входит в (1/2) допуска"

    @JsonProperty("PH_23SGP")
    private Long ph23sgp;// "Процент длины полосы, на которой толщина входит в (2/3) допуска"

    @JsonProperty("lclThckng")
    private Long[] lclThckng;// "Высота местных утолщений по ширине полосы"

    @JsonProperty("estimate")
    private Long estimate;// "Оценка годности полосы"
}
