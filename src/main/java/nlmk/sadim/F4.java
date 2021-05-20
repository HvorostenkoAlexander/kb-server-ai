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
public class F4 {

    /**
     * Время входа полосы в клеть №9 стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    private Date enter;
    /**
     * Время выхода полосы из клети №9 стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("EXIT")
    private Date exit;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Время входа полосы в клеть №9 стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    public Date getEnter() {
        return enter;
    }

    /**
     * Время входа полосы в клеть №9 стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("ENTER")
    public void setEnter(Date enter) {
        this.enter = enter;
    }

    /**
     * Время выхода полосы из клети №9 стана 2000
     * <p>
     *
     *
     */
    @JsonProperty("EXIT")
    public Date getExit() {
        return exit;
    }

    /**
     * Время выхода полосы из клети №9 стана 2000
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
