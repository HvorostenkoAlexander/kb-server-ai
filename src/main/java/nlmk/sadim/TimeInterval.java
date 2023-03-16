package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "COIL",
        "F1",
        "F2",
        "F3",
        "F4",
        "F5",
        "F6",
        "F7",
        "R1_1",
        "R1_2",
        "R1_3",
        "R2",
        "R3",
        "R4",
        "R5"
})
@Generated("jsonschema2pojo")
public class TimeInterval {

    @JsonProperty("COIL")
    private Coil coil;
    @JsonProperty("F1")
    private F1 f1;
    @JsonProperty("F2")
    private F2 f2;
    @JsonProperty("F3")
    private F3 f3;
    @JsonProperty("F4")
    private F4 f4;
    @JsonProperty("F5")
    private F5 f5;
    @JsonProperty("F6")
    private F6 f6;
    @JsonProperty("F7")
    private F7 f7;
    @JsonProperty("R1_1")
    private R11 r11;
    @JsonProperty("R1_2")
    private R12 r12;
    @JsonProperty("R1_3")
    private R13 r13;
    @JsonProperty("R2")
    private R2__1 r2;
    @JsonProperty("R3")
    private R3 r3;
    @JsonProperty("R4")
    private R4__1 r4;
    @JsonProperty("R5")
    private R5__1 r5;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("COIL")
    public Coil getCoil() {
        return coil;
    }

    @JsonProperty("COIL")
    public void setCoil(Coil coil) {
        this.coil = coil;
    }

    @JsonProperty("F1")
    public F1 getF1() {
        return f1;
    }

    @JsonProperty("F1")
    public void setF1(F1 f1) {
        this.f1 = f1;
    }

    @JsonProperty("F2")
    public F2 getF2() {
        return f2;
    }

    @JsonProperty("F2")
    public void setF2(F2 f2) {
        this.f2 = f2;
    }

    @JsonProperty("F3")
    public F3 getF3() {
        return f3;
    }

    @JsonProperty("F3")
    public void setF3(F3 f3) {
        this.f3 = f3;
    }

    @JsonProperty("F4")
    public F4 getF4() {
        return f4;
    }

    @JsonProperty("F4")
    public void setF4(F4 f4) {
        this.f4 = f4;
    }

    @JsonProperty("F5")
    public F5 getF5() {
        return f5;
    }

    @JsonProperty("F5")
    public void setF5(F5 f5) {
        this.f5 = f5;
    }

    @JsonProperty("F6")
    public F6 getF6() {
        return f6;
    }

    @JsonProperty("F6")
    public void setF6(F6 f6) {
        this.f6 = f6;
    }

    @JsonProperty("F7")
    public F7 getF7() {
        return f7;
    }

    @JsonProperty("F7")
    public void setF7(F7 f7) {
        this.f7 = f7;
    }

    @JsonProperty("R1_1")
    public R11 getR11() {
        return r11;
    }

    @JsonProperty("R1_1")
    public void setR11(R11 r11) {
        this.r11 = r11;
    }

    @JsonProperty("R1_2")
    public R12 getR12() {
        return r12;
    }

    @JsonProperty("R1_2")
    public void setR12(R12 r12) {
        this.r12 = r12;
    }

    @JsonProperty("R1_3")
    public R13 getR13() {
        return r13;
    }

    @JsonProperty("R1_3")
    public void setR13(R13 r13) {
        this.r13 = r13;
    }

    @JsonProperty("R2")
    public R2__1 getR2() {
        return r2;
    }

    @JsonProperty("R2")
    public void setR2(R2__1 r2) {
        this.r2 = r2;
    }

    @JsonProperty("R3")
    public R3 getR3() {
        return r3;
    }

    @JsonProperty("R3")
    public void setR3(R3 r3) {
        this.r3 = r3;
    }

    @JsonProperty("R4")
    public R4__1 getR4() {
        return r4;
    }

    @JsonProperty("R4")
    public void setR4(R4__1 r4) {
        this.r4 = r4;
    }

    @JsonProperty("R5")
    public R5__1 getR5() {
        return r5;
    }

    @JsonProperty("R5")
    public void setR5(R5__1 r5) {
        this.r5 = r5;
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