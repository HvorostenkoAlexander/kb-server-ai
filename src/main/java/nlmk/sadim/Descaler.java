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
 * Установки гидросбивов окалины в линии стана
 * <p>
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "VSB",
        "R1",
        "R2",
        "R4",
        "R5",
        "HSB"
})
@Generated("jsonschema2pojo")
public class Descaler {

    /**
     * Гидросбив окалины после вертикального окалиноломателя
     * <p>
     *
     *
     */
    @JsonProperty("VSB")
    private Vsb vsb;
    /**
     * Гидросбив окалины после 1-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R1")
    private R1 r1;
    /**
     * Гидросбив окалины после 2-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R2")
    private R2 r2;
    /**
     * Гидросбив окалины перед 4-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R4")
    private R4 r4;
    /**
     * Гидросбив окалины перед 5-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R5")
    private R5 r5;
    /**
     * Гидросбив окалины перед чистовой группой стана
     * <p>
     *
     *
     */
    @JsonProperty("HSB")
    private Hsb hsb;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Гидросбив окалины после вертикального окалиноломателя
     * <p>
     *
     *
     */
    @JsonProperty("VSB")
    public Vsb getVsb() {
        return vsb;
    }

    /**
     * Гидросбив окалины после вертикального окалиноломателя
     * <p>
     *
     *
     */
    @JsonProperty("VSB")
    public void setVsb(Vsb vsb) {
        this.vsb = vsb;
    }

    /**
     * Гидросбив окалины после 1-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R1")
    public R1 getR1() {
        return r1;
    }

    /**
     * Гидросбив окалины после 1-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R1")
    public void setR1(R1 r1) {
        this.r1 = r1;
    }

    /**
     * Гидросбив окалины после 2-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R2")
    public R2 getR2() {
        return r2;
    }

    /**
     * Гидросбив окалины после 2-ой клети стана
     * <p>
     *
     *
     */
    @JsonProperty("R2")
    public void setR2(R2 r2) {
        this.r2 = r2;
    }

    /**
     * Гидросбив окалины перед 4-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R4")
    public R4 getR4() {
        return r4;
    }

    /**
     * Гидросбив окалины перед 4-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R4")
    public void setR4(R4 r4) {
        this.r4 = r4;
    }

    /**
     * Гидросбив окалины перед 5-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R5")
    public R5 getR5() {
        return r5;
    }

    /**
     * Гидросбив окалины перед 5-ой клетью стана
     * <p>
     *
     *
     */
    @JsonProperty("R5")
    public void setR5(R5 r5) {
        this.r5 = r5;
    }

    /**
     * Гидросбив окалины перед чистовой группой стана
     * <p>
     *
     *
     */
    @JsonProperty("HSB")
    public Hsb getHsb() {
        return hsb;
    }

    /**
     * Гидросбив окалины перед чистовой группой стана
     * <p>
     *
     *
     */
    @JsonProperty("HSB")
    public void setHsb(Hsb hsb) {
        this.hsb = hsb;
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
