package com.nlmk.kb.server.api.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MechanicalData {

    private Integer mechCode;//Код характеристики
    private String mechName;//Наименование характеристики
    private String mechFormat;//формат
    private String mechValue;//значение
    private Integer mechTypeCode;//Тип данных (1 - строка, 2 - число, 3 - дата)
    private String mechMeasure;//единица измерения

}