package com.nlmk.kb.server.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sadim_pre_attestation_param")
public class PreAttestationParam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @JsonProperty("primeId")
    @Column(name = "prime_id")
    private String primeId; //"идентификатор"

    @JsonProperty("t12_min")
    @Column(name = "t12_min")
    private Double t12Min;// U "Температура конца прокатки (мin)"

    @JsonProperty("t12_max")
    @Column(name = "t12_max")
    private Double t12Max;// U "Температура конца прокатки (мах)"

    @JsonProperty("tcm_min")
    @Column(name = "tcm_min")
    private Double tcmMin;// U "Температура смотки (мin)"

    @JsonProperty("tcm_max")
    @Column(name = "tcm_max")
    private Double tcmMax;// U "Температура смотки (мах)"

    @JsonProperty("PBI")
    @Column(name = "pbi")
    private Double pbi;// "Процент длины полосы, на которой ширина в допуске"

    @JsonProperty("ProfFact")
    @Column(name = "prof_fact")
    private Double profFact;// U "Профиль"

    @JsonProperty("WedgeFact")
    @Column(name = "wedge_fact")
    private Double wedgeFact;// U "Клин"

    @JsonProperty("SQC_CRIT_MAX")
    @Column(name = "sqc_crit_max")
    private Double sqcCritMax;// U "Наибольшая критичность дефекта на полосе"

    @JsonProperty("PH_1SGP")
    @Column(name = "ph1_sgp")
    private Double ph1sgp;// U "Процент длины полосы, на которой толщина входит в полный допуск"

    @JsonProperty("PH_12SGP")
    @Column(name = "ph12_sgp")
    private String ph12sgp;// U "Процент длины полосы, на которой толщина входит в (1/2) допуска"

    @JsonProperty("PH_23SGP")
    @Column(name = "ph23_sgp")
    private Double ph23sgp;// U "Процент длины полосы, на которой толщина входит в (2/3) допуска"

    @JsonProperty("lclThckng")
    @Column(name = "lclthckng")
    private String lclThckng;// "Высота местных утолщений по ширине полосы"

    @JsonProperty("estimate")
    @Column(name = "estimate")
    private Integer estimate;// "Оценка годности полосы"

    @JsonProperty("lot_no")
    @Column(name = "lot_no")
    private Integer lotNo;// Номер горячекатаной партии

    @JsonProperty("melt_no")
    @Column(name = "melt_no")
    private Integer meltNo; // Номер плавки
}
