package nlmk.sadim;

import com.fasterxml.jackson.annotation.*;
import lombok.ToString;

import javax.annotation.Generated;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "chemical",
        "furnace",
        "idedm",
        "lot_no",
        "melt_no",
        "order_finish",
        "slab",
        "advice",
        "steel_grade",
        "strips",
        "time_load",
        "time_unload"
})
@Generated("jsonschema2pojo")
@ToString
public class Sadim {

    @JsonProperty("chemical")
    private Chemical chemical;

    /**
     * Номер нагревательной печи в которой находился сляб
     * <p>
     */
    @JsonProperty("furnace")
    private Integer furnace;

    /**
     * Идентификатор сляба 3-го уровня
     * <p>
     */
    @JsonProperty("idedm")
    private String idedm;

    /**
     * Номер горячекатаной партии
     * <p>
     */
    @JsonProperty("lot_no")
    private Integer lotNo;

    /**
     * Номер плавки
     * <p>
     */
    @JsonProperty("melt_no")
    private Integer meltNo;

    @JsonProperty("order_finish")
    private OrderFinish orderFinish;

    @JsonProperty("slab")
    private Slab slab;

    @JsonProperty("advice")
    private List<Advice> advice = null;

    /**
     * Марка стали из сменно-суточного графика прокатки
     * <p>
     */
    @JsonProperty("steel_grade")
    private String steelGrade;

    @JsonProperty("strips")
    private List<Strip> strips = null;

    /**
     * Время загрузки сляба в печь
     * <p>
     */
    @JsonProperty("time_load")
    private Date timeLoad;

    /**
     * Время выгрузки сляба из печи
     * <p>
     */
    @JsonProperty("time_unload")
    private Date timeUnload;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("chemical")
    public Chemical getChemical() {
        return chemical;
    }

    @JsonProperty("chemical")
    public void setChemical(Chemical chemical) {
        this.chemical = chemical;
    }

    /**
     * Номер нагревательной печи в которой находился сляб
     * <p>
     */
    @JsonProperty("furnace")
    public Integer getFurnace() {
        return furnace;
    }

    /**
     * Номер нагревательной печи в которой находился сляб
     * <p>
     */
    @JsonProperty("furnace")
    public void setFurnace(Integer furnace) {
        this.furnace = furnace;
    }

    /**
     * Идентификатор сляба 3-го уровня
     * <p>
     */
    @JsonProperty("idedm")
    public String getIdedm() {
        return idedm;
    }

    /**
     * Идентификатор сляба 3-го уровня
     * <p>
     */
    @JsonProperty("idedm")
    public void setIdedm(String idedm) {
        this.idedm = idedm;
    }

    /**
     * Номер горячекатаной партии
     * <p>
     */
    @JsonProperty("lot_no")
    public Integer getLotNo() {
        return lotNo;
    }

    /**
     * Номер горячекатаной партии
     * <p>
     */
    @JsonProperty("lot_no")
    public void setLotNo(Integer lotNo) {
        this.lotNo = lotNo;
    }

    /**
     * Номер плавки
     * <p>
     */
    @JsonProperty("melt_no")
    public Integer getMeltNo() {
        return meltNo;
    }

    /**
     * Номер плавки
     * <p>
     */
    @JsonProperty("melt_no")
    public void setMeltNo(Integer meltNo) {
        this.meltNo = meltNo;
    }

    @JsonProperty("order_finish")
    public OrderFinish getOrderFinish() {
        return orderFinish;
    }

    @JsonProperty("order_finish")
    public void setOrderFinish(OrderFinish orderFinish) {
        this.orderFinish = orderFinish;
    }

    @JsonProperty("slab")
    public Slab getSlab() {
        return slab;
    }

    @JsonProperty("slab")
    public void setSlab(Slab slab) {
        this.slab = slab;
    }

    @JsonProperty("advice")
    public List<Advice> getAdvice() {
        return advice;
    }

    @JsonProperty("advice")
    public void setAdvice(List<Advice> advice) {
        this.advice = advice;
    }

    /**
     * Марка стали из сменно-суточного графика прокатки
     * <p>
     */
    @JsonProperty("steel_grade")
    public String getSteelGrade() {
        return steelGrade;
    }

    /**
     * Марка стали из сменно-суточного графика прокатки
     * <p>
     */
    @JsonProperty("steel_grade")
    public void setSteelGrade(String steelGrade) {
        this.steelGrade = steelGrade;
    }

    @JsonProperty("strips")
    public List<Strip> getStrips() {
        return strips;
    }

    @JsonProperty("strips")
    public void setStrips(List<Strip> strips) {
        this.strips = strips;
    }

    /**
     * Время загрузки сляба в печь
     * <p>
     */
    @JsonProperty("time_load")
    public Date getTimeLoad() {
        return timeLoad;
    }

    /**
     * Время загрузки сляба в печь
     * <p>
     */
    @JsonProperty("time_load")
    public void setTimeLoad(Date timeLoad) {
        this.timeLoad = timeLoad;
    }

    /**
     * Время выгрузки сляба из печи
     * <p>
     */
    @JsonProperty("time_unload")
    public Date getTimeUnload() {
        return timeUnload;
    }

    /**
     * Время выгрузки сляба из печи
     * <p>
     */
    @JsonProperty("time_unload")
    public void setTimeUnload(Date timeUnload) {
        this.timeUnload = timeUnload;
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
