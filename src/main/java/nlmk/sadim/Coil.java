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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "ENTER",
        "EXIT"
})
@Generated("jsonschema2pojo")
public class Coil {

    /**
     * Время начала смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    private Date enter;
    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("EXIT")
    private Date exit;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Время начала смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    public Date getEnter() {
        return enter;
    }

    /**
     * Время начала смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    public void setEnter(Date enter) {
        this.enter = enter;
    }

    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("EXIT")
    public Date getExit() {
        return exit;
    }

    /**
     * Время окончания смотки полосы
     * <p>
     *
     *
     */
    @JsonProperty("EXIT")
    public void setExit(Date exit) {
        this.exit = exit;
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