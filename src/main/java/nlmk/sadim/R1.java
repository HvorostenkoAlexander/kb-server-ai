package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;


/**
 * Гидросбив окалины после 1-ой клети стана
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
public class R1 {

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