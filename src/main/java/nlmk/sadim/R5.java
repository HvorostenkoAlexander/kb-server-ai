package nlmk.sadim;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Гидросбив окалины перед 5-ой клетью стана
 * <p>
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "TIME_ON",
        "TIME_OFF",
        "STATE"
})
@Generated("jsonschema2pojo")
public class R5 {

    /**
     * Задержка на включение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_ON")
    private Integer timeOn;
    /**
     * Задержка на выключение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_OFF")
    private Integer timeOff;
    /**
     * Признак включения на голове полосы
     * <p>
     *
     *
     */
    @JsonProperty("STATE")
    private Integer state;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Задержка на включение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_ON")
    public Integer getTimeOn() {
        return timeOn;
    }

    /**
     * Задержка на включение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_ON")
    public void setTimeOn(Integer timeOn) {
        this.timeOn = timeOn;
    }

    /**
     * Задержка на выключение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_OFF")
    public Integer getTimeOff() {
        return timeOff;
    }

    /**
     * Задержка на выключение
     * <p>
     *
     *
     */
    @JsonProperty("TIME_OFF")
    public void setTimeOff(Integer timeOff) {
        this.timeOff = timeOff;
    }

    /**
     * Признак включения на голове полосы
     * <p>
     *
     *
     */
    @JsonProperty("STATE")
    public Integer getState() {
        return state;
    }

    /**
     * Признак включения на голове полосы
     * <p>
     *
     *
     */
    @JsonProperty("STATE")
    public void setState(Integer state) {
        this.state = state;
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
