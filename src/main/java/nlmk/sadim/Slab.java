package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "length",
        "thickness",
        "weight",
        "width"
})
@Generated("jsonschema2pojo")
public class Slab {

    /**
     * Длина сляба
     * <p>
     *
     *
     */
    @JsonProperty("length")
    private Integer length;
    /**
     * Толщина сляба
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    private Integer thickness;
    /**
     * Вес сляба
     * <p>
     *
     *
     */
    @JsonProperty("weight")
    private Double weight;
    /**
     * Ширина сляба
     * <p>
     *
     *
     */
    @JsonProperty("width")
    private Integer width;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Длина сляба
     * <p>
     *
     *
     */
    @JsonProperty("length")
    public Integer getLength() {
        return length;
    }

    /**
     * Длина сляба
     * <p>
     *
     *
     */
    @JsonProperty("length")
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * Толщина сляба
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public Integer getThickness() {
        return thickness;
    }

    /**
     * Толщина сляба
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    /**
     * Вес сляба
     * <p>
     *
     *
     */
    @JsonProperty("weight")
    public Double getWeight() {
        return weight;
    }

    /**
     * Вес сляба
     * <p>
     *
     *
     */
    @JsonProperty("weight")
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    /**
     * Ширина сляба
     * <p>
     *
     *
     */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    /**
     * Ширина сляба
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
