package com.nlmk.kb.server.entity.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataField {

    private String primeId;//Идентификатор ЕМ на стане
    private Long nplv;//номер плавки
    private Long hnum;//Номер ГК партии
    private String roll;//номер рулона/пачки
    private Double length;//длина
    private Double thickness;//толщина
    private Double width;//ширина
    private Double weightNet;//масса единицы продукции
    private Double bundleWeight; // масса связки
    private Long kceh;//номер цеха
    private Long orderNum;//номер заказа
    private Long orderPos;//номер позиции заказа

    private List<Specs> specifications;//Основные характеристики единицы продукции
    private List<OrderRequest> orderReq;//Требования заказа
    private List<ChemicalSpec> chemical;
    private List<MechanicalSpec> mechanical;
    private List<MetallographicSpec> metallographic;//Металлографическая оценка стали

}