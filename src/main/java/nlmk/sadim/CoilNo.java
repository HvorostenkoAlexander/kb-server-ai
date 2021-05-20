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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "roll_camp",
        "typesize"
})
@Generated("jsonschema2pojo")
public class CoilNo {

    /**
     * Номер полосы в кампании рабочих валков
     * <p>
     *
     *
     */
    @JsonProperty("roll_camp")
    private Integer rollCamp;
    /**
     * Номер полосы в типоразмере
     * <p>
     *
     *
     */
    @JsonProperty("typesize")
    private Integer typesize;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Номер полосы в кампании рабочих валков
     * <p>
     *
     *
     */
    @JsonProperty("roll_camp")
    public Integer getRollCamp() {
        return rollCamp;
    }

    /**
     * Номер полосы в кампании рабочих валков
     * <p>
     *
     *
     */
    @JsonProperty("roll_camp")
    public void setRollCamp(Integer rollCamp) {
        this.rollCamp = rollCamp;
    }

    /**
     * Номер полосы в типоразмере
     * <p>
     *
     *
     */
    @JsonProperty("typesize")
    public Integer getTypesize() {
        return typesize;
    }

    /**
     * Номер полосы в типоразмере
     * <p>
     *
     *
     */
    @JsonProperty("typesize")
    public void setTypesize(Integer typesize) {
        this.typesize = typesize;
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
