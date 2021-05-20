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
        "thickness",
        "width"
})
@Generated("jsonschema2pojo")
public class Target {

    /**
     * Заданная толщина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    private Double thickness;
    /**
     * Заданная ширина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("width")
    private Integer width;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Заданная толщина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public Double getThickness() {
        return thickness;
    }

    /**
     * Заданная толщина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    /**
     * Заданная ширина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    /**
     * Заданная ширина полосы при прокатке в чистовой группе
     * <p>
     *
     *
     */
    @JsonProperty("width")
    public void setWidth(Integer width) {
        this.width = width;
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
