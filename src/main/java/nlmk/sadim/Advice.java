package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "RateFurnace",
        "RollingPauseTask",
        "RollingPauseAdd",
        "RollingTime",
        "TYPE_ADVICE",
        "ROLL_PAUSE",
        "REAL_PAUSE",
        "RATE_110",
        "MODE"
})
@Generated("jsonschema2pojo")
public class Advice {

    /**
     * Задание на темп прокатки по печам
     * <p>
     *
     *
     */
    @JsonProperty("RateFurnace")
    private Integer rateFurnace;
    /**
     * Расчётная пауза
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseTask")
    private Integer rollingPauseTask;
    /**
     * Добавка на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseAdd")
    private Integer rollingPauseAdd;
    /**
     * Расчётное время прокатки
     * <p>
     *
     *
     */
    @JsonProperty("RollingTime")
    private Integer rollingTime;
    /**
     * Тип рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("TYPE_ADVICE")
    private Integer typeAdvice;
    /**
     * Заданная пауза в чистовой группе между полосами
     * <p>
     *
     *
     */
    @JsonProperty("ROLL_PAUSE")
    private Integer rollPause;
    /**
     * Заданная пауза на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("REAL_PAUSE")
    private Integer realPause;
    /**
     * Задание на темп 110 рольганга
     * <p>
     *
     *
     */
    @JsonProperty("RATE_110")
    private Integer rate110;
    /**
     * Режим работы Сервиса рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("MODE")
    private Integer mode;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Задание на темп прокатки по печам
     * <p>
     *
     *
     */
    @JsonProperty("RateFurnace")
    public Integer getRateFurnace() {
        return rateFurnace;
    }

    /**
     * Задание на темп прокатки по печам
     * <p>
     *
     *
     */
    @JsonProperty("RateFurnace")
    public void setRateFurnace(Integer rateFurnace) {
        this.rateFurnace = rateFurnace;
    }

    /**
     * Расчётная пауза
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseTask")
    public Integer getRollingPauseTask() {
        return rollingPauseTask;
    }

    /**
     * Расчётная пауза
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseTask")
    public void setRollingPauseTask(Integer rollingPauseTask) {
        this.rollingPauseTask = rollingPauseTask;
    }

    /**
     * Добавка на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseAdd")
    public Integer getRollingPauseAdd() {
        return rollingPauseAdd;
    }

    /**
     * Добавка на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("RollingPauseAdd")
    public void setRollingPauseAdd(Integer rollingPauseAdd) {
        this.rollingPauseAdd = rollingPauseAdd;
    }

    /**
     * Расчётное время прокатки
     * <p>
     *
     *
     */
    @JsonProperty("RollingTime")
    public Integer getRollingTime() {
        return rollingTime;
    }

    /**
     * Расчётное время прокатки
     * <p>
     *
     *
     */
    @JsonProperty("RollingTime")
    public void setRollingTime(Integer rollingTime) {
        this.rollingTime = rollingTime;
    }

    /**
     * Тип рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("TYPE_ADVICE")
    public Integer getTypeAdvice() {
        return typeAdvice;
    }

    /**
     * Тип рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("TYPE_ADVICE")
    public void setTypeAdvice(Integer typeAdvice) {
        this.typeAdvice = typeAdvice;
    }

    /**
     * Заданная пауза в чистовой группе между полосами
     * <p>
     *
     *
     */
    @JsonProperty("ROLL_PAUSE")
    public Integer getRollPause() {
        return rollPause;
    }

    /**
     * Заданная пауза в чистовой группе между полосами
     * <p>
     *
     *
     */
    @JsonProperty("ROLL_PAUSE")
    public void setRollPause(Integer rollPause) {
        this.rollPause = rollPause;
    }

    /**
     * Заданная пауза на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("REAL_PAUSE")
    public Integer getRealPause() {
        return realPause;
    }

    /**
     * Заданная пауза на перестройку
     * <p>
     *
     *
     */
    @JsonProperty("REAL_PAUSE")
    public void setRealPause(Integer realPause) {
        this.realPause = realPause;
    }

    /**
     * Задание на темп 110 рольганга
     * <p>
     *
     *
     */
    @JsonProperty("RATE_110")
    public Integer getRate110() {
        return rate110;
    }

    /**
     * Задание на темп 110 рольганга
     * <p>
     *
     *
     */
    @JsonProperty("RATE_110")
    public void setRate110(Integer rate110) {
        this.rate110 = rate110;
    }

    /**
     * Режим работы Сервиса рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("MODE")
    public Integer getMode() {
        return mode;
    }

    /**
     * Режим работы Сервиса рекомендаций
     * <p>
     *
     *
     */
    @JsonProperty("MODE")
    public void setMode(Integer mode) {
        this.mode = mode;
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