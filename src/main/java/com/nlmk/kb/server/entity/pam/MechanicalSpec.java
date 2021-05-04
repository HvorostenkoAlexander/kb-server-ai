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
public class MechanicalSpec {

    private Integer testArrayId;//Идентификатор испытательного массива
    private Integer hnum;//Номер испытуемой горячекатаной партии
    private Integer protId;//ИД протокола
    private Integer protNum;//Номер протокола
    private Integer sampleId;//ИД пробы
    private String probeName;//Наименование вида пробы
    private Integer probeCode;//Код вида пробы
    private Integer sampleNum;//Номер пробы (образца)

    /**
     * Признак испытаний
     * (1 - первичные,
     *  2 - повторные,
     *  3 - первич с другой маркой,
     *  4 - повторн с другой маркой,
     *  5 - стат. расчет,
     *  6 - первич с третьей маркой)
     */
    private Integer signAnalysis;
    private String formationListId;//ИД формировочной карточки
    private Integer formationListNum;//Номер формировочной карточки
    private List<MechanicalData> mechData;//Характеристики и результаты мех испытаний

}