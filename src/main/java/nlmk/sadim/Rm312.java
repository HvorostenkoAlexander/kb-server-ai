package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Данные от профилимера, установленного после чистовой группы стана
 * <p>
 *
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "headers",
        "values"
})
@Generated("jsonschema2pojo")
public class Rm312 {

    @JsonProperty("headers")
    private List<Header__1> headers = null;

    @JsonProperty("values")
    private List<List<Double>> values = null;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("headers")
    public List<Header__1> getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(List<Header__1> headers) {
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
