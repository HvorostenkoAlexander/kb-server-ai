package com.nlmk.kb.server.entity.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Integer attrCode;//Код характеристики
    private String attrName;//Наименование характеристики
    private String attrValue;//Значение характеристики
    private Integer attrTypeCode;//Тип данных (1 - строка, 2 - число, 3 - дата)
    private String attrFormat;//Формат передачи характеристики
    private String attrMeasure;//единица измерения

}