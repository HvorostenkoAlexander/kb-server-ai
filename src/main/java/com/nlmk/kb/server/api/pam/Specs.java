package com.nlmk.kb.server.api.pam;

import com.nlmk.attestation.product.api.Group;
import com.nlmk.attestation.product.api.Params;
import com.nlmk.attestation.product.api.SpecificationDto;
import com.nlmk.attestation.product.api.Status;
import com.nlmk.attestation.product.api.TypeCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Объект API, список параметров для прохождения Аттестации<br/>
 * (Нет прямой связи с объектом SpecificationDto)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Specs {

    private Integer specCode; //код характеристики
    private String specName; // Наименование характеристики
    private String specValue;//значение характеристики
    private Integer specTypeCode;//Тип данных (1 - строка, 2 - число, 3 - дата)
    private String specFormat;//формат характеристики
    private String specMeasure;//единица измерения

    private Group group; // ранее Type
    private Params params;
    private Status status;
    private String equals;
    private Double min;
    private Double max;
    private String comment;

    public SpecificationDto toSpecDto() {
        return SpecificationDto.builder()
                .code(specCode)
                .format(specFormat)
                .value(specValue)
                .measure(specMeasure)
                .typeCode(TypeCode.fromValue(specTypeCode))
                .build();
    }

}