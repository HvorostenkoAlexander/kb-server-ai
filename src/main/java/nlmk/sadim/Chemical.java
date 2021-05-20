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
        "AL",
        "AS",
        "B",
        "BI",
        "C",
        "CA",
        "CD",
        "CO",
        "CR",
        "CU",
        "F",
        "FE",
        "H",
        "MG",
        "MN",
        "MO",
        "N",
        "NB",
        "NI",
        "O",
        "P",
        "PB",
        "S",
        "SB",
        "SE",
        "SI",
        "SN",
        "TA",
        "TI",
        "VA",
        "W",
        "ZN",
        "ZR"
})
@Generated("jsonschema2pojo")
public class Chemical {

    /**
     * Процентное содержание алюминия
     * <p>
     *
     *
     */
    @JsonProperty("AL")
    private Double al;
    /**
     * Процентное содержание мышьяка
     * <p>
     *
     *
     */
    @JsonProperty("AS")
    private Double as;
    /**
     * Процентное содержание бора
     * <p>
     *
     *
     */
    @JsonProperty("B")
    private Double b;
    /**
     * Процентное содержание висмута
     * <p>
     *
     *
     */
    @JsonProperty("BI")
    private Double bi;
    /**
     * Процентное содержание углерода
     * <p>
     *
     *
     */
    @JsonProperty("C")
    private Double c;
    /**
     * Процентное содержание кальция
     * <p>
     *
     *
     */
    @JsonProperty("CA")
    private Double ca;
    /**
     * Процентное содержание кадмия
     * <p>
     *
     *
     */
    @JsonProperty("CD")
    private Double cd;
    /**
     * Процентное содержание кобальта
     * <p>
     *
     *
     */
    @JsonProperty("CO")
    private Double co;
    /**
     * Процентное содержание хрома
     * <p>
     *
     *
     */
    @JsonProperty("CR")
    private Double cr;
    /**
     * Процентное содержание меди
     * <p>
     *
     *
     */
    @JsonProperty("CU")
    private Double cu;
    /**
     * Процентное содержание фтора
     * <p>
     *
     *
     */
    @JsonProperty("F")
    private Double f;
    /**
     * Процентное содержание железа
     * <p>
     *
     *
     */
    @JsonProperty("FE")
    private Double fe;
    /**
     * Процентное содержание водорода
     * <p>
     *
     *
     */
    @JsonProperty("H")
    private Double h;
    /**
     * Процентное содержание магния
     * <p>
     *
     *
     */
    @JsonProperty("MG")
    private Double mg;
    /**
     * Процентное содержание марганца
     * <p>
     *
     *
     */
    @JsonProperty("MN")
    private Double mn;
    /**
     * Процентное содержание молибдена
     * <p>
     *
     *
     */
    @JsonProperty("MO")
    private Double mo;
    /**
     * Процентное содержание азота
     * <p>
     *
     *
     */
    @JsonProperty("N")
    private Double n;
    /**
     * Процентное содержание ниобия
     * <p>
     *
     *
     */
    @JsonProperty("NB")
    private Double nb;
    /**
     * Процентное содержание никеля
     * <p>
     *
     *
     */
    @JsonProperty("NI")
    private Double ni;
    /**
     * Процентное содержание кислорода
     * <p>
     *
     *
     */
    @JsonProperty("O")
    private Double o;
    /**
     * Процентное содержание фосфора
     * <p>
     *
     *
     */
    @JsonProperty("P")
    private Double p;
    /**
     * Процентное содержание свинца
     * <p>
     *
     *
     */
    @JsonProperty("PB")
    private Double pb;
    /**
     * Процентное содержание серы
     * <p>
     *
     *
     */
    @JsonProperty("S")
    private Double s;
    /**
     * Процентное содержание сурьмы
     * <p>
     *
     *
     */
    @JsonProperty("SB")
    private Double sb;
    /**
     * Процентное содержание селена
     * <p>
     *
     *
     */
    @JsonProperty("SE")
    private Double se;
    /**
     * Процентное содержание кремния
     * <p>
     *
     *
     */
    @JsonProperty("SI")
    private Double si;
    /**
     * Процентное содержание олова
     * <p>
     *
     *
     */
    @JsonProperty("SN")
    private Double sn;
    /**
     * Процентное содержание тантала
     * <p>
     *
     *
     */
    @JsonProperty("TA")
    private Double ta;
    /**
     * Процентное содержание титана
     * <p>
     *
     *
     */
    @JsonProperty("TI")
    private Double ti;
    /**
     * Процентное содержание ванадия
     * <p>
     *
     *
     */
    @JsonProperty("VA")
    private Double va;
    /**
     * Процентное содержание вольфрама
     * <p>
     *
     *
     */
    @JsonProperty("W")
    private Double w;
    /**
     * Процентное содержание цинка
     * <p>
     *
     *
     */
    @JsonProperty("ZN")
    private Double zn;
    /**
     * Процентное содержание циркония
     * <p>
     *
     *
     */
    @JsonProperty("ZR")
    private Double zr;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * Процентное содержание алюминия
     * <p>
     *
     *
     */
    @JsonProperty("AL")
    public Double getAl() {
        return al;
    }

    /**
     * Процентное содержание алюминия
     * <p>
     *
     *
     */
    @JsonProperty("AL")
    public void setAl(Double al) {
        this.al = al;
    }

    /**
     * Процентное содержание мышьяка
     * <p>
     *
     *
     */
    @JsonProperty("AS")
    public Double getAs() {
        return as;
    }

    /**
     * Процентное содержание мышьяка
     * <p>
     *
     *
     */
    @JsonProperty("AS")
    public void setAs(Double as) {
        this.as = as;
    }

    /**
     * Процентное содержание бора
     * <p>
     *
     *
     */
    @JsonProperty("B")
    public Double getB() {
        return b;
    }

    /**
     * Процентное содержание бора
     * <p>
     *
     *
     */
    @JsonProperty("B")
    public void setB(Double b) {
        this.b = b;
    }

    /**
     * Процентное содержание висмута
     * <p>
     *
     *
     */
    @JsonProperty("BI")
    public Double getBi() {
        return bi;
    }

    /**
     * Процентное содержание висмута
     * <p>
     *
     *
     */
    @JsonProperty("BI")
    public void setBi(Double bi) {
        this.bi = bi;
    }

    /**
     * Процентное содержание углерода
     * <p>
     *
     *
     */
    @JsonProperty("C")
    public Double getC() {
        return c;
    }

    /**
     * Процентное содержание углерода
     * <p>
     *
     *
     */
    @JsonProperty("C")
    public void setC(Double c) {
        this.c = c;
    }

    /**
     * Процентное содержание кальция
     * <p>
     *
     *
     */
    @JsonProperty("CA")
    public Double getCa() {
        return ca;
    }

    /**
     * Процентное содержание кальция
     * <p>
     *
     *
     */
    @JsonProperty("CA")
    public void setCa(Double ca) {
        this.ca = ca;
    }

    /**
     * Процентное содержание кадмия
     * <p>
     *
     *
     */
    @JsonProperty("CD")
    public Double getCd() {
        return cd;
    }

    /**
     * Процентное содержание кадмия
     * <p>
     *
     *
     */
    @JsonProperty("CD")
    public void setCd(Double cd) {
        this.cd = cd;
    }

    /**
     * Процентное содержание кобальта
     * <p>
     *
     *
     */
    @JsonProperty("CO")
    public Double getCo() {
        return co;
    }

    /**
     * Процентное содержание кобальта
     * <p>
     *
     *
     */
    @JsonProperty("CO")
    public void setCo(Double co) {
        this.co = co;
    }

    /**
     * Процентное содержание хрома
     * <p>
     *
     *
     */
    @JsonProperty("CR")
    public Double getCr() {
        return cr;
    }

    /**
     * Процентное содержание хрома
     * <p>
     *
     *
     */
    @JsonProperty("CR")
    public void setCr(Double cr) {
        this.cr = cr;
    }

    /**
     * Процентное содержание меди
     * <p>
     *
     *
     */
    @JsonProperty("CU")
    public Double getCu() {
        return cu;
    }

    /**
     * Процентное содержание меди
     * <p>
     *
     *
     */
    @JsonProperty("CU")
    public void setCu(Double cu) {
        this.cu = cu;
    }

    /**
     * Процентное содержание фтора
     * <p>
     *
     *
     */
    @JsonProperty("F")
    public Double getF() {
        return f;
    }

    /**
     * Процентное содержание фтора
     * <p>
     *
     *
     */
    @JsonProperty("F")
    public void setF(Double f) {
        this.f = f;
    }

    /**
     * Процентное содержание железа
     * <p>
     *
     *
     */
    @JsonProperty("FE")
    public Double getFe() {
        return fe;
    }

    /**
     * Процентное содержание железа
     * <p>
     *
     *
     */
    @JsonProperty("FE")
    public void setFe(Double fe) {
        this.fe = fe;
    }

    /**
     * Процентное содержание водорода
     * <p>
     *
     *
     */
    @JsonProperty("H")
    public Double getH() {
        return h;
    }

    /**
     * Процентное содержание водорода
     * <p>
     *
     *
     */
    @JsonProperty("H")
    public void setH(Double h) {
        this.h = h;
    }

    /**
     * Процентное содержание магния
     * <p>
     *
     *
     */
    @JsonProperty("MG")
    public Double getMg() {
        return mg;
    }

    /**
     * Процентное содержание магния
     * <p>
     *
     *
     */
    @JsonProperty("MG")
    public void setMg(Double mg) {
        this.mg = mg;
    }

    /**
     * Процентное содержание марганца
     * <p>
     *
     *
     */
    @JsonProperty("MN")
    public Double getMn() {
        return mn;
    }

    /**
     * Процентное содержание марганца
     * <p>
     *
     *
     */
    @JsonProperty("MN")
    public void setMn(Double mn) {
        this.mn = mn;
    }

    /**
     * Процентное содержание молибдена
     * <p>
     *
     *
     */
    @JsonProperty("MO")
    public Double getMo() {
        return mo;
    }

    /**
     * Процентное содержание молибдена
     * <p>
     *
     *
     */
    @JsonProperty("MO")
    public void setMo(Double mo) {
        this.mo = mo;
    }

    /**
     * Процентное содержание азота
     * <p>
     *
     *
     */
    @JsonProperty("N")
    public Double getN() {
        return n;
    }

    /**
     * Процентное содержание азота
     * <p>
     *
     *
     */
    @JsonProperty("N")
    public void setN(Double n) {
        this.n = n;
    }

    /**
     * Процентное содержание ниобия
     * <p>
     *
     *
     */
    @JsonProperty("NB")
    public Double getNb() {
        return nb;
    }

    /**
     * Процентное содержание ниобия
     * <p>
     *
     *
     */
    @JsonProperty("NB")
    public void setNb(Double nb) {
        this.nb = nb;
    }

    /**
     * Процентное содержание никеля
     * <p>
     *
     *
     */
    @JsonProperty("NI")
    public Double getNi() {
        return ni;
    }

    /**
     * Процентное содержание никеля
     * <p>
     *
     *
     */
    @JsonProperty("NI")
    public void setNi(Double ni) {
        this.ni = ni;
    }

    /**
     * Процентное содержание кислорода
     * <p>
     *
     *
     */
    @JsonProperty("O")
    public Double getO() {
        return o;
    }

    /**
     * Процентное содержание кислорода
     * <p>
     *
     *
     */
    @JsonProperty("O")
    public void setO(Double o) {
        this.o = o;
    }

    /**
     * Процентное содержание фосфора
     * <p>
     *
     *
     */
    @JsonProperty("P")
    public Double getP() {
        return p;
    }

    /**
     * Процентное содержание фосфора
     * <p>
     *
     *
     */
    @JsonProperty("P")
    public void setP(Double p) {
        this.p = p;
    }

    /**
     * Процентное содержание свинца
     * <p>
     *
     *
     */
    @JsonProperty("PB")
    public Double getPb() {
        return pb;
    }

    /**
     * Процентное содержание свинца
     * <p>
     *
     *
     */
    @JsonProperty("PB")
    public void setPb(Double pb) {
        this.pb = pb;
    }

    /**
     * Процентное содержание серы
     * <p>
     *
     *
     */
    @JsonProperty("S")
    public Double getS() {
        return s;
    }

    /**
     * Процентное содержание серы
     * <p>
     *
     *
     */
    @JsonProperty("S")
    public void setS(Double s) {
        this.s = s;
    }

    /**
     * Процентное содержание сурьмы
     * <p>
     *
     *
     */
    @JsonProperty("SB")
    public Double getSb() {
        return sb;
    }

    /**
     * Процентное содержание сурьмы
     * <p>
     *
     *
     */
    @JsonProperty("SB")
    public void setSb(Double sb) {
        this.sb = sb;
    }

    /**
     * Процентное содержание селена
     * <p>
     *
     *
     */
    @JsonProperty("SE")
    public Double getSe() {
        return se;
    }

    /**
     * Процентное содержание селена
     * <p>
     *
     *
     */
    @JsonProperty("SE")
    public void setSe(Double se) {
        this.se = se;
    }

    /**
     * Процентное содержание кремния
     * <p>
     *
     *
     */
    @JsonProperty("SI")
    public Double getSi() {
        return si;
    }

    /**
     * Процентное содержание кремния
     * <p>
     *
     *
     */
    @JsonProperty("SI")
    public void setSi(Double si) {
        this.si = si;
    }

    /**
     * Процентное содержание олова
     * <p>
     *
     *
     */
    @JsonProperty("SN")
    public Double getSn() {
        return sn;
    }

    /**
     * Процентное содержание олова
     * <p>
     *
     *
     */
    @JsonProperty("SN")
    public void setSn(Double sn) {
        this.sn = sn;
    }

    /**
     * Процентное содержание тантала
     * <p>
     *
     *
     */
    @JsonProperty("TA")
    public Double getTa() {
        return ta;
    }

    /**
     * Процентное содержание тантала
     * <p>
     *
     *
     */
    @JsonProperty("TA")
    public void setTa(Double ta) {
        this.ta = ta;
    }

    /**
     * Процентное содержание титана
     * <p>
     *
     *
     */
    @JsonProperty("TI")
    public Double getTi() {
        return ti;
    }

    /**
     * Процентное содержание титана
     * <p>
     *
     *
     */
    @JsonProperty("TI")
    public void setTi(Double ti) {
        this.ti = ti;
    }

    /**
     * Процентное содержание ванадия
     * <p>
     *
     *
     */
    @JsonProperty("VA")
    public Double getVa() {
        return va;
    }

    /**
     * Процентное содержание ванадия
     * <p>
     *
     *
     */
    @JsonProperty("VA")
    public void setVa(Double va) {
        this.va = va;
    }

    /**
     * Процентное содержание вольфрама
     * <p>
     *
     *
     */
    @JsonProperty("W")
    public Double getW() {
        return w;
    }

    /**
     * Процентное содержание вольфрама
     * <p>
     *
     *
     */
    @JsonProperty("W")
    public void setW(Double w) {
        this.w = w;
    }

    /**
     * Процентное содержание цинка
     * <p>
     *
     *
     */
    @JsonProperty("ZN")
    public Double getZn() {
        return zn;
    }

    /**
     * Процентное содержание цинка
     * <p>
     *
     *
     */
    @JsonProperty("ZN")
    public void setZn(Double zn) {
        this.zn = zn;
    }

    /**
     * Процентное содержание циркония
     * <p>
     *
     *
     */
    @JsonProperty("ZR")
    public Double getZr() {
        return zr;
    }

    /**
     * Процентное содержание циркония
     * <p>
     *
     *
     */
    @JsonProperty("ZR")
    public void setZr(Double zr) {
        this.zr = zr;
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
