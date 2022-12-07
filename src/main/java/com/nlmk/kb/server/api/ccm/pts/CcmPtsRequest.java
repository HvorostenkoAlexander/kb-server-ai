package com.nlmk.kb.server.api.ccm.pts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.nlmk.attestation.product.api.pam.AnalysisValue;
import com.nlmk.attestation.product.api.specification.TypeCode;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;

/**
 * Запрос на Аттестацию Единицы Продукции, цех ЦТС<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034208">Аттестация ЕП ЦТС</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcmPtsRequest {

    @NotBlank
    private String ts; // Дата и время передачи
    @NotNull
    private Pk pk; // Первичный ключ
    @Valid
    private Record data; // Данные

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk {
        @NotBlank
        private String systemCode; // Код системы
        @NotBlank
        private String id; // ИД единицы металла (ЕМ)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        @NotNull
        private Integer werks; // Код завода
        @NotBlank
        private String werksName; // Наименование завода
        @NotNull
        private Integer kceh; // Код цеха
        @NotBlank
        private String kcehName; // Наименование цеха
        @NotNull
        private Integer unitCode; // Код агрегата
        @NotBlank
        private String unitName; // Наименование агрегата
        @NotNull
        private Integer storageCode; // Код склада
        @NotBlank
        private String storageName; // Наименование склада
        @NotNull
        @Valid
        private Marking marking; // Маркировка ЕМ
        @NotNull
        private Double weightNet; // Вес нетто (т.)
        @NotNull
        @Valid
        private Geometry geometry; // Геометрия
        private Long orderNum; // Номер заказа
        private Integer orderPos; // Позиция заказа
        private Integer unionId; // Идентификатор упаковки бунтов
        private List<@Valid Bundle> bundles; // Список бунтов входящих в одну связку
        @NotEmpty
        private List<@Valid Specification> specifications; // Список дополнительных характеристик
        private List<@Valid OneProperty> properties; // Результаты магнитных свойств
        private List<@Valid Chemical> chemical; // Химия
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Marking {
        @NotNull
        private Integer nplv; // Номер плавки
        @NotNull
        private Integer hnum; // Номер ГК партии
        @NotNull
        private Integer tnum; // Номер ХК партии
        @NotNull
        private Integer roll; // Номер рулона
        private Integer strip; // Номер бунта
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Geometry {
        @NotNull
        private Double thickness; // Толщина, мм
        @NotNull
        private Double width; // Ширина, мм
        @NotNull
        private Double length; // Длина, мм
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bundle {
        @NotNull
        private Long stripId; // Идентификатор бунта
        @NotNull
        private Integer stripNum; // Номер бунта
        @NotNull
        private Double stripWidth; // Ширина бунта
        @NotNull
        private Double stripWeight; // Вес бунта (т.)
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Specification {
        @NotNull
        private Integer specCode; // Код характеристики
        @NotBlank
        private String specName; // Наименование характеристики
        @NotNull
        private Integer specTypeCode; // Тип данных
        @NotBlank
        private String specTypeName; // Наименование типа данных
        @NotNull
        private SpecTypeValue specTypeValue; // Тип значения (1 - простое, 2 - перечислимое)
        private String specValue; // Значение
        private List<@Valid OneSpecValue> listValues;
        private String specDecryption; // Расшифровка справочного значения
        private String specFormat; // Формат передачи характеристики
        private String specMeasure; // Единица измерения
    }

    @Getter
    @AllArgsConstructor
    public enum SpecTypeValue {

        SIMPLE(1, "простое"),
        ENUMERABLE(2, "перечислимое");

        @JsonValue
        private final Integer value;
        private final String desc;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static SpecTypeValue fromValue(int value) {
            return Arrays.stream(SpecTypeValue.values())
                    .filter(s -> s.getValue().equals(value))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown SpecTypeValue value [%s]", value)));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OneSpecValue {
        @NotBlank
        private String value; // Значение
        private String description; // Описание справочного значения
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OneProperty {
        @NotNull
        private Integer probeCode; // Код вида пробы
        @NotNull
        private String probeName; // Наименование вида пробы
        @NotNull
        private String testDate; // Дата и время испытания YYYY-MM-DD"T"HH24:MI:SS+/-HH:MM
        @NotNull
        private Integer typeCode; // Код типа испытания
        @NotBlank
        private String typeName; // Наименование типа испытания
        @NotEmpty
        private List<@Valid OnePropAnalyze> analyzes; // Анализы
        private List<@Valid OnePropValue> listValues; // Дополнительные результаты
        private List<@Valid OnePropAtt> attestationList; // Список аттестаций
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnePropValue {
        @NotNull
        private Integer attrCode; // Код атрибута
        @NotNull
        private TypeCode attrType; // Тип атрибута (1 - Строка, 2 - Число, 3 - Дата)
        private List<String> attrValue; // Значение атрибута
        private String attrFormat; // Формат атрибута
        private String attrMeasure; // Единица измерения атрибута
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnePropAnalyze {
        @NotNull
        private Integer samplingPlaceCode; // Код места отбора пробы
        @NotBlank
        private String samplingPlaceName; // Наименование места отбора пробы
        @NotNull
        private AnalysisValue analysisValue; // Результат (1-Худший, 2-Лучший)
        @NotEmpty
        private List<@Valid OneAnalyzeValue> listValues; // Список значений
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OneAnalyzeValue {
        @NotNull
        private Integer attrCode; // Код атрибута
        @NotNull
        private TypeCode attrType; // Тип атрибута (1 - Строка, 2 - Число, 3 - Дата)
        private String attrValue; // Значение атрибута
        private String attrFormat; // Формат атрибута
        private String attrMeasure; // Единица измерения атрибута
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnePropAtt {
        @NotNull
        private Integer typeCode; // Код аттестации*
        @NotBlank
        private String typeName; // Наименование аттестации
        private List<@Valid OneAttValue> listValues; // Список значений
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OneAttValue {
        @NotNull
        private Side side; // Сторона (1 - Лицевая, 2 - Обратная, 3 - Обе стороны)
        @NotNull
        private Integer attrCode; // Код атрибута
        @NotNull
        private Double attrValue; // Значение атрибута
    }

    @Getter
    @AllArgsConstructor
    public enum Side {

        FRONT(1, "Лицевая"),
        BACK(2, "Обратная"),
        BOTH_SIDES(3, "Обе стороны");

        @JsonValue
        private final Integer value;
        private final String desc;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static Side fromValue(int value) {
            return Arrays.stream(Side.values())
                    .filter(s -> s.getValue().equals(value))
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown Side value [%s]", value)));
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chemical {
        @NotNull
        private Integer id; // Идентификатор хим анализа
        private List<@Valid OneChemicalValue> listValues; // Список хим.элементов
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OneChemicalValue {
        @NotNull
        private Integer code; // Код химического элемента
        @NotBlank
        private String name; // Наименование химического элемента
        private Double value; // Значение химического элемента
    }

}
