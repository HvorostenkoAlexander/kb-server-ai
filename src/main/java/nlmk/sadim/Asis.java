package nlmk.sadim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Список дефектов от системы контроля качества поверхности (после чистовой группы стана)
 * <p>
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "estimate",
        "headers",
        "values"
})
@Generated("jsonschema2pojo")
public class Asis {

    /**
     * Оценка годности полосы
     * <p>
     *
     *
     */
    @JsonProperty("estimate")
    private Integer estimate;
    @JsonProperty("headers")
    private List<Header> headers = null;
    @JsonProperty("values")
    private List<List<Double>> values = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Оценка годности полосы
     * <p>
     *
     *
     */
    @JsonProperty("estimate")
    public Integer getEstimate() {
        return estimate;
    }

    /**
     * Оценка годности полосы
     * <p>
     *
     *
     */
    @JsonProperty("estimate")
    public void setEstimate(Integer estimate) {
        this.estimate = estimate;
    }

    @JsonProperty("headers")
    public List<Header> getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    @JsonProperty("values")
    public List<List<Double>> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<List<Double>> values) {
        this.values = values;
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
