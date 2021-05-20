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
 * Список локальных утолщений
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
public class LclThckng {

    @JsonProperty("headers")
    private List<Header__2> headers = null;
    @JsonProperty("values")
    private List<List<Double>> values = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("headers")
    public List<Header__2> getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(List<Header__2> headers) {
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
