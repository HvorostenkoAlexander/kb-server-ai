package com.nlmk.kb.server.api.ccm.phpp;


import com.nlmk.kb.server.api.ccm.SpecTypeValue;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * Запрос на Аттестацию Единицы Продукции, цех ЦХПП<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=266329364">АСАП - ССМ ЦХПП</a>
 */

@Data
@Builder
@Jacksonized
public class CcmPhppRequest {

    private final @NotBlank String ts; // Дата и время передачи
    private final @NotNull @Valid Pk pk; // Первичный ключ
    private final @Valid Record data; // Данные

    @Data
    @Builder
    @Jacksonized
    public static class Pk {
        private final @NotBlank String systemCode; // Код системы
        private final @NotBlank String id; // ИД единицы металла (ЕМ)
    }

    @Data
    @Builder
    @Jacksonized
    @SuppressWarnings("checkstyle:illegalidentifiername")
    public static class Record {
        private final Integer heat; // № плавки
        private final Integer hnum; // Номер ГК партии
        private final Integer tnum; // Номер ХК партии
        private final @NotNull Integer roll; // Номер рулона/пачки
        private final BigDecimal length; // Длина
        private final @NotNull BigDecimal thickness; // Толщина
        private final @NotNull BigDecimal width; // Ширина
        private final @NotNull BigDecimal weightNet; // Масса единицы продукции
        private final @NotNull Integer workshopNum; // Номер цеха
        private final Long orderNum; // Номер заказа
        private final Integer orderPos; // Номер позиции заказа
        private final Integer attestationPoint; // Точка аттестации (6 - Аттестация перед порезкой, 7 - Аттестация формировочной, 8 - Аттестация сертификата)
        private final @NotEmpty List<@Valid Specification> specifications; // Основные характеристики единицы продукции
        private final List<@Valid Chemical> chemical; // 	Химический анализ плавки
        private final List<@Valid Mechanic> mechanical; // Физ. мех. свойства
    }

    @Data
    @Builder
    @Jacksonized
    public static class Mechanic {
        private final Integer hnum; // Номер испытуемой горячекатаной партии
        private final Integer tnum; // Номер испытуемой холоднокатаной партии
        private final Integer protNum; // Номер протокола
        private final String protDate; // Дата протокола
        private final Integer sampleNum; // Номер пробы (образца)
        private final Integer signAnalysis; // Признак испытаний
        private final @NotEmpty List<@Valid MechanicData> mechData; // 	Данные мех. испытаний
    }

    @Data
    @Builder
    @Jacksonized
    public static class MechanicData {
        private final Integer analysisId; // Id анализа
        private final @NotEmpty List<@Valid TestData> testData; // Данные анализа мех. испытаний
    }

    @Data
    @Builder
    @Jacksonized
    public static class TestData {
        private final @NotNull Integer mechCode; // Код характеристики
        private final @NotBlank String mechName; // Наименование характеристики
        private final String mechValue; // Значение характеристики
        private final @NotNull Integer mechTypeCode; // Тип данных
    }

    @Data
    @Builder
    @Jacksonized
    public static class Chemical {
        private final @NotNull Integer chemCode; // Код характеристики
        private final @NotBlank String chemName; // Наименование характеристики
        private final String chemValue; // Значение характеристики
    }

    @Data
    @Builder
    @Jacksonized
    public static class Specification {
        private final @NotNull Integer specCode; // Код характеристики
        private final @NotBlank String specName; // Наименование характеристики
        private final String specValue; // Значение
        private final @NotNull Integer specTypeCode; // Тип данных
        private final @NotBlank String specTypeName; // Наименование типа данных
        private final @NotNull SpecTypeValue specTypeValue; // Тип значения (1 - простое, 2 - перечислимое)
        private final List<@Valid OneSpecValue> listValues;
    }

    @Data
    @Builder
    @Jacksonized
    public static class OneSpecValue {
        private final @NotBlank String value; // Значение
    }

}
