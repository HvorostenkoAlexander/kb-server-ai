package com.nlmk.kb.server.api.ccm.kc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nlmk.attestation.product.api.specification.TypeCode;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ на Аттестацию Единицы Продукции, цех ЦТС<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=166240974">Аттестация ЕП КЦ1,КЦ2 [2.2]</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcmKc1Response {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO8601
    private Date ts;
    @NotNull
    private Pk pk; // Первичный ключ
    private Record data; // Данные

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pk {
        @NotBlank
        private String systemCode; // Код системы(31)
        @NotBlank
        private String id; // ИД результата аттестации
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Record {
        @NotBlank
        private String primeSystemCode; // Система - первоисточник запроса на аттестацию
        @NotBlank
        private String primeId; // Идентификатор (ИД) единицы ЕМ в первоисточнике
        @NotNull
        private Mismatch mismatch; // Результат проверки ЕМ
        @NotEmpty
        private List<Attestation> attestationList; // Группы характеристик с результатами проверки
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Mismatch {
        @NotNull
        private Integer code; // Код результата аттестации
        @NotBlank
        private String name; // Название результата аттестации
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attestation {
        @NotNull
        private Integer groupCode; // Код группы характеристик
        @NotBlank
        private String groupName; // Название группы характеристик
        @NotEmpty
        private List<AttestationValue> listValues; // Результаты проверки характеристик из группы
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttestationValue {
        @NotNull
        private Integer code; // Код характеристики
        @NotBlank
        private String name; // Наименование характеристики
        @NotNull
        private TypeCode typeCode; // Тип данных (1-строка, 2-число, 3-дата)
        @NotBlank
        private String typeName; // Наименование типа данных
        private String value; // Значение характеристики
        private String format; // Формат передачи характеристики
        private String measure; // Единица измерения
        private NormLimit normLimits; // С чем сверяли фактическое значение
        @NotNull
        private Mismatch mismatch; // Результаты проверки параметра
        private String note; // Пояснение результата проверки характеристики
        private String defectSuggestion; // Текст рекомендации по устранению (не соответствия)
        private List<Parameter> parameters; // Список параметров для аттестуемой характеристики
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NormLimit {
        List<AccValue> listAccValues; // Перечень допустимых значений
        private Double valueMin; // Минимальное допустимое значение
        private Double valueMax; // Максимальное допустимое значение
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccValue {
        @NotBlank
        private String value; // Допустимое значение
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Parameter {
        @NotNull
        private Integer code; // Код параметра
        @NotBlank
        private String name; // Название параметра
        @NotBlank
        private String value; // Значение параметра
        @NotNull
        private TypeCode typeCode; // Тип параметра (1-строка, 2-число, 3-дата)
        @NotBlank
        private String typeName; // Наименование типа параметра
    }

}
