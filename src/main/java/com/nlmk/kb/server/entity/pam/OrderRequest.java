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
public class OrderRequest {

    private Integer attrCode;//Код характеристики
    private String attrName;//Наименование характеристики
    private String attrValue;//Значение характеристики
    private Integer attrTypeCode;//Тип данных (1 - строка, 2 - число, 3 - дата)
 //   private Integer attrTypeValue; // Тип значения (1 - простое, 2 - перечисленное)
    private String attrFormat;//Формат передачи характеристики
    private String attrMeasure;//единица измерения
 //   private List<String> recordListValue;//Список значений параметра

}