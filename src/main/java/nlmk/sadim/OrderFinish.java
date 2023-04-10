package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "thickness",
        "width"
})
@Generated("jsonschema2pojo")
public class OrderFinish {

    /**
     * Заданная толщина рулона по сменно-суточному графику прокатки
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    private Double thickness;
    /**
     * Заданная ширина рулона по сменно-суточному графику прокатки
     * <p>
     *
     *
     */
    @JsonProperty("width")
    private Integer width;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Заданная толщина рулона по сменно-суточному графику прокатки
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public Double getThickness() {
        return thickness;
    }

    /**
     * Заданная толщина рулона по сменно-суточному графику прокатки
     * <p>
     *
     *
     */
    @JsonProperty("thickness")
    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    /**
     * Заданная ширина рулона по сменно-суточному графику прокатки
     * <p>
     *
     *
     */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    /**
     * Заданная ширина рулона по сменно-суточному графику прокатки
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
