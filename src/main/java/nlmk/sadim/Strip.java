package nlmk.sadim;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ID2",
        "PRIME_ID",
        "DESCALER",
        "TIME_INTERVAL",
        "asis",
        "coil_no",
        "coil_weight",
        "coiler",
        "rm312",
        "specific_tension",
        "steel_grade",
        "strip_length",
        "target",
        "tension_percent",
        "rolling_time_fact",
        "rolling_time_calc",
        "rolling_pause_fact",
        "rolling_pause_calc",
        "time_coil",
        "time_dead",
        "time_rolling",
        "yield_stress",
        "t12_min",
        "t12_max",
        "tcm_min",
        "tcm_max",
        "PBI",
        "ProfFact",
        "WedgeFact",
        "SQC_CRIT_MAX",
        "PH_1SGP",
        "PH_12SGP",
        "PH_23SGP",
        "lclThckng"
})
@Generated("jsonschema2pojo")
@ToString
public class Strip {

    /**
     * Идентификатор 2-го уровня для полосы
     * <p>
     *
     *
     */
    @JsonProperty("ID2")
    private Long id2;
    /**
     * Идентификатор MES
     * <p>
     *
     *
     */
    @JsonProperty("PRIME_ID")
    private String primeId;
    /**
     * Установки гидросбивов окалины в линии стана
     * <p>
     *
     *
     */
    @JsonProperty("DESCALER")
    private Descaler descaler;
    @JsonProperty("TIME_INTERVAL")
    private TimeInterval timeInterval;
    /**
     * Список дефектов от системы контроля качества поверхности (после чистовой группы стана)
     * <p>
     *
     *
     */
    @JsonProperty("asis")
    private Asis asis;

    @JsonProperty("coil_no")
    private CoilNo coilNo;
    /**
     * Измеренный вес рулона
     * <p>
     *
     *
     */
    @JsonProperty("coil_weight")
    private Double coilWeight;
    /**
     * Номер моталки на которую смотана полоса
     * <p>
     *
     *
     */
    @JsonProperty("coiler")
    private Long coiler;
    /**
     * Данные от профилимера, установленного после чистовой группы стана
     * <p>
     *
     *
     */
   // @JsonProperty("rm312")
//    @JsonIgnore
//    private Rm312 rm312;
    /**
     * Удельное натяжение полосы на моталке
     * <p>
     *
     *
     */
    @JsonProperty("specific_tension")
    private Long specificTension;
    /**
     * Марка стали для прокатки (марка стали по выплавке)
     * <p>
     *
     *
     */
    @JsonProperty("steel_grade")
    private String steelGrade;
    /**
     * Расчётная длина полосы
     * <p>
     *
     *
     */
    @JsonProperty("strip_length")
    private Double stripLength;
    @JsonProperty("target")
    private Target target;
    /**
     * Процент перераспределения натяжения между моталкой и чистовой группой
     * <p>
     *
     *
     */
    @JsonProperty("tension_percent")
    private Long tensionPercent;
    /**
     * Фактическое время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_fact")
    private Long rollingTimeFact;
    /**
     * Расчётное время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_calc")
    private Long rollingTimeCalc;
    /**
     * Фактическая пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_fact")
    private Long rollingPauseFact;
    /**
     * Расчётная пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_calc")
    private Long rollingPauseCalc;
    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("time_coil")
    private Date timeCoil;
    /**
     * Время окончания обработки на стане 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_dead")
    private Date timeDead;
    /**
     * Время прокатки полосы в чистовой группе стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_rolling")
    private Date timeRolling;
    /**
     * Расчётный предел текучести полосы при смотке в рулон
     * <p>
     *
     *
     */
    @JsonProperty("yield_stress")
    private Double yieldStress;
    /**
     * Уставка на Ткп (min)
     * <p>
     *
     *
     */
    @JsonProperty("t12_min")
    private Double t12Min;
    /**
     * Уставка на Ткп (max)
     * <p>
     *
     *
     */
    @JsonProperty("t12_max")
    private Double t12Max;
    /**
     * Уставка на Тсм (min)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_min")
    private Double tcmMin;
    /**
     * Уставка на Тсм (max)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_max")
    private Double tcmMax;
    /**
     * B12 - % длины полосы, в допуске
     * <p>
     *
     *
     */
    @JsonProperty("PBI")
    private Double pbi;
    /**
     * Величина среднего профиля (выпуклости) полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("ProfFact")
    private Double profFact;
    /**
     * Величина средней клиновидности полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("WedgeFact")
    private Double wedgeFact;
    /**
     * Наибольшая критичность дефекта на полосе
     * <p>
     *
     *
     */
    @JsonProperty("SQC_CRIT_MAX")
    private Double sqcCritMax;
    /**
     * H12 - % длины полосы, в полном допуске
     * <p>
     *
     *
     */
    @JsonProperty("PH_1SGP")
    private Double ph1sgp;
    /**
     * H12 - % длины полосы, в 1/2 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_12SGP")
    private Double ph12sgp;
    /**
     * H12 - % длины полосы, в 2/3 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_23SGP")
    private Double ph23sgp;
    /**
     * Список локальных утолщений
     * <p>
     *
     *
     */
    @JsonProperty("lclThckng")
    private LclThckng lclThckng;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Идентификатор 2-го уровня для полосы
     * <p>
     *
     *
     */
    @JsonProperty("ID2")
    public Long getId2() {
        return id2;
    }

    /**
     * Идентификатор 2-го уровня для полосы
     * <p>
     *
     *
     */
    @JsonProperty("ID2")
    public void setId2(Long id2) {
        this.id2 = id2;
    }

    /**
     * Идентификатор MES
     * <p>
     *
     *
     */
    @JsonProperty("PRIME_ID")
    public String getPrimeId() {
        return primeId;
    }

    /**
     * Идентификатор MES
     * <p>
     *
     *
     */
    @JsonProperty("PRIME_ID")
    public void setPrimeId(String primeId) {
        this.primeId = primeId;
    }

    /**
     * Установки гидросбивов окалины в линии стана
     * <p>
     *
     *
     */
    @JsonProperty("DESCALER")
    public Descaler getDescaler() {
        return descaler;
    }

    /**
     * Установки гидросбивов окалины в линии стана
     * <p>
     *
     *
     */
    @JsonProperty("DESCALER")
    public void setDescaler(Descaler descaler) {
        this.descaler = descaler;
    }

    @JsonProperty("TIME_INTERVAL")
    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    @JsonProperty("TIME_INTERVAL")
    public void setTimeInterval(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
    }

    /**
     * Список дефектов от системы контроля качества поверхности (после чистовой группы стана)
     * <p>
     *
     *
     */
    @JsonProperty("asis")
    public Asis getAsis() {
        return asis;
    }

    /**
     * Список дефектов от системы контроля качества поверхности (после чистовой группы стана)
     * <p>
     *
     *
     */
    @JsonProperty("asis")
    public void setAsis(Asis asis) {
        this.asis = asis;
    }

    @JsonProperty("coil_no")
    public CoilNo getCoilNo() {
        return coilNo;
    }

    @JsonProperty("coil_no")
    public void setCoilNo(CoilNo coilNo) {
        this.coilNo = coilNo;
    }

    /**
     * Измеренный вес рулона
     * <p>
     *
     *
     */
    @JsonProperty("coil_weight")
    public Double getCoilWeight() {
        return coilWeight;
    }

    /**
     * Измеренный вес рулона
     * <p>
     *
     *
     */
    @JsonProperty("coil_weight")
    public void setCoilWeight(Double coilWeight) {
        this.coilWeight = coilWeight;
    }

    /**
     * Номер моталки на которую смотана полоса
     * <p>
     *
     *
     */
    @JsonProperty("coiler")
    public Long getCoiler() {
        return coiler;
    }

    /**
     * Номер моталки на которую смотана полоса
     * <p>
     *
     *
     */
    @JsonProperty("coiler")
    public void setCoiler(Long coiler) {
        this.coiler = coiler;
    }

    /**
     * Данные от профилимера, установленного после чистовой группы стана
     * <p>
     *
     *
     */
//    @JsonProperty("rm312")
//    public Rm312 getRm312() {
//        return rm312;
//    }

    /**
     * Данные от профилимера, установленного после чистовой группы стана
     * <p>
     *
     *
     */
//    @JsonProperty("rm312")
//    public void setRm312(Rm312 rm312) {
//        this.rm312 = rm312;
//    }

    /**
     * Удельное натяжение полосы на моталке
     * <p>
     *
     *
     */
    @JsonProperty("specific_tension")
    public Long getSpecificTension() {
        return specificTension;
    }

    /**
     * Удельное натяжение полосы на моталке
     * <p>
     *
     *
     */
    @JsonProperty("specific_tension")
    public void setSpecificTension(Long specificTension) {
        this.specificTension = specificTension;
    }

    /**
     * Марка стали для прокатки (марка стали по выплавке)
     * <p>
     *
     *
     */
    @JsonProperty("steel_grade")
    public String getSteelGrade() {
        return steelGrade;
    }

    /**
     * Марка стали для прокатки (марка стали по выплавке)
     * <p>
     *
     *
     */
    @JsonProperty("steel_grade")
    public void setSteelGrade(String steelGrade) {
        this.steelGrade = steelGrade;
    }

    /**
     * Расчётная длина полосы
     * <p>
     *
     *
     */
    @JsonProperty("strip_length")
    public Double getStripLength() {
        return stripLength;
    }

    /**
     * Расчётная длина полосы
     * <p>
     *
     *
     */
    @JsonProperty("strip_length")
    public void setStripLength(Double stripLength) {
        this.stripLength = stripLength;
    }

    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Процент перераспределения натяжения между моталкой и чистовой группой
     * <p>
     *
     *
     */
    @JsonProperty("tension_percent")
    public Long getTensionPercent() {
        return tensionPercent;
    }

    /**
     * Процент перераспределения натяжения между моталкой и чистовой группой
     * <p>
     *
     *
     */
    @JsonProperty("tension_percent")
    public void setTensionPercent(Long tensionPercent) {
        this.tensionPercent = tensionPercent;
    }

    /**
     * Фактическое время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_fact")
    public Long getRollingTimeFact() {
        return rollingTimeFact;
    }

    /**
     * Фактическое время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_fact")
    public void setRollingTimeFact(Long rollingTimeFact) {
        this.rollingTimeFact = rollingTimeFact;
    }

    /**
     * Расчётное время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_calc")
    public Long getRollingTimeCalc() {
        return rollingTimeCalc;
    }

    /**
     * Расчётное время прокатки в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_time_calc")
    public void setRollingTimeCalc(Long rollingTimeCalc) {
        this.rollingTimeCalc = rollingTimeCalc;
    }

    /**
     * Фактическая пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_fact")
    public Long getRollingPauseFact() {
        return rollingPauseFact;
    }

    /**
     * Фактическая пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_fact")
    public void setRollingPauseFact(Long rollingPauseFact) {
        this.rollingPauseFact = rollingPauseFact;
    }

    /**
     * Расчётная пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_calc")
    public Long getRollingPauseCalc() {
        return rollingPauseCalc;
    }

    /**
     * Расчётная пауза при прокатке в чистовой группе стана
     * <p>
     *
     *
     */
    @JsonProperty("rolling_pause_calc")
    public void setRollingPauseCalc(Long rollingPauseCalc) {
        this.rollingPauseCalc = rollingPauseCalc;
    }

    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("time_coil")
    public Date getTimeCoil() {
        return timeCoil;
    }

    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("time_coil")
    public void setTimeCoil(Date timeCoil) {
        this.timeCoil = timeCoil;
    }

    /**
     * Время окончания обработки на стане 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_dead")
    public Date getTimeDead() {
        return timeDead;
    }

    /**
     * Время окончания обработки на стане 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_dead")
    public void setTimeDead(Date timeDead) {
        this.timeDead = timeDead;
    }

    /**
     * Время прокатки полосы в чистовой группе стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_rolling")
    public Date getTimeRolling() {
        return timeRolling;
    }

    /**
     * Время прокатки полосы в чистовой группе стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("time_rolling")
    public void setTimeRolling(Date timeRolling) {
        this.timeRolling = timeRolling;
    }

    /**
     * Расчётный предел текучести полосы при смотке в рулон
     * <p>
     *
     *
     */
    @JsonProperty("yield_stress")
    public Double getYieldStress() {
        return yieldStress;
    }

    /**
     * Расчётный предел текучести полосы при смотке в рулон
     * <p>
     *
     *
     */
    @JsonProperty("yield_stress")
    public void setYieldStress(Double yieldStress) {
        this.yieldStress = yieldStress;
    }

    /**
     * Уставка на Ткп (min)
     * <p>
     *
     *
     */
    @JsonProperty("t12_min")
    public Double getT12Min() {
        return t12Min;
    }

    /**
     * Уставка на Ткп (min)
     * <p>
     *
     *
     */
    @JsonProperty("t12_min")
    public void setT12Min(Double t12Min) {
        this.t12Min = t12Min;
    }

    /**
     * Уставка на Ткп (max)
     * <p>
     *
     *
     */
    @JsonProperty("t12_max")
    public Double getT12Max() {
        return t12Max;
    }

    /**
     * Уставка на Ткп (max)
     * <p>
     *
     *
     */
    @JsonProperty("t12_max")
    public void setT12Max(Double t12Max) {
        this.t12Max = t12Max;
    }

    /**
     * Уставка на Тсм (min)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_min")
    public Double getTcmMin() {
        return tcmMin;
    }

    /**
     * Уставка на Тсм (min)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_min")
    public void setTcmMin(Double tcmMin) {
        this.tcmMin = tcmMin;
    }

    /**
     * Уставка на Тсм (max)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_max")
    public Double getTcmMax() {
        return tcmMax;
    }

    /**
     * Уставка на Тсм (max)
     * <p>
     *
     *
     */
    @JsonProperty("tcm_max")
    public void setTcmMax(Double tcmMax) {
        this.tcmMax = tcmMax;
    }

    /**
     * B12 - % длины полосы, в допуске
     * <p>
     *
     *
     */
    @JsonProperty("PBI")
    public Double getPbi() {
        return pbi;
    }

    /**
     * B12 - % длины полосы, в допуске
     * <p>
     *
     *
     */
    @JsonProperty("PBI")
    public void setPbi(Double pbi) {
        this.pbi = pbi;
    }

    /**
     * Величина среднего профиля (выпуклости) полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("ProfFact")
    public Double getProfFact() {
        return profFact;
    }

    /**
     * Величина среднего профиля (выпуклости) полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("ProfFact")
    public void setProfFact(Double profFact) {
        this.profFact = profFact;
    }

    /**
     * Величина средней клиновидности полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("WedgeFact")
    public Double getWedgeFact() {
        return wedgeFact;
    }

    /**
     * Величина средней клиновидности полосы (SIPRO)
     * <p>
     *
     *
     */
    @JsonProperty("WedgeFact")
    public void setWedgeFact(Double wedgeFact) {
        this.wedgeFact = wedgeFact;
    }

    /**
     * Наибольшая критичность дефекта на полосе
     * <p>
     *
     *
     */
    @JsonProperty("SQC_CRIT_MAX")
    public Double getSqcCritMax() {
        return sqcCritMax;
    }

    /**
     * Наибольшая критичность дефекта на полосе
     * <p>
     *
     *
     */
    @JsonProperty("SQC_CRIT_MAX")
    public void setSqcCritMax(Double sqcCritMax) {
        this.sqcCritMax = sqcCritMax;
    }

    /**
     * H12 - % длины полосы, в полном допуске
     * <p>
     *
     *
     */
    @JsonProperty("PH_1SGP")
    public Double getPh1sgp() {
        return ph1sgp;
    }

    /**
     * H12 - % длины полосы, в полном допуске
     * <p>
     *
     *
     */
    @JsonProperty("PH_1SGP")
    public void setPh1sgp(Double ph1sgp) {
        this.ph1sgp = ph1sgp;
    }

    /**
     * H12 - % длины полосы, в 1/2 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_12SGP")
    public Double getPh12sgp() {
        return ph12sgp;
    }

    /**
     * H12 - % длины полосы, в 1/2 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_12SGP")
    public void setPh12sgp(Double ph12sgp) {
        this.ph12sgp = ph12sgp;
    }

    /**
     * H12 - % длины полосы, в 2/3 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_23SGP")
    public Double getPh23sgp() {
        return ph23sgp;
    }

    /**
     * H12 - % длины полосы, в 2/3 допуска
     * <p>
     *
     *
     */
    @JsonProperty("PH_23SGP")
    public void setPh23sgp(Double ph23sgp) {
        this.ph23sgp = ph23sgp;
    }

    /**
     * Список локальных утолщений
     * <p>
     *
     *
     */
    @JsonProperty("lclThckng")
    public LclThckng getLclThckng() {
        return lclThckng;
    }

    /**
     * Список локальных утолщений
     * <p>
     *
     *
     */
    @JsonProperty("lclThckng")
    public void setLclThckng(LclThckng lclThckng) {
        this.lclThckng = lclThckng;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
