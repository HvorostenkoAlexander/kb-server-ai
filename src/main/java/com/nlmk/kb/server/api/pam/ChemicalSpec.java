package com.nlmk.kb.server.api.pam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChemicalSpec {

    private Integer chemCode;//Код характеристики
    private String chemName;//Наименование характеристики
    private String chemValue;//Значение характеристики
    private String chemFormat;//Формат передачи характеристики

}