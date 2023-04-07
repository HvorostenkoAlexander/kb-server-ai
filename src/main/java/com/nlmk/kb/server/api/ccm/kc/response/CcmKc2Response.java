package com.nlmk.kb.server.api.ccm.kc.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotNull;

import com.nlmk.kb.server.api.ccm.kc.Pk;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * Ответ на Аттестацию Единицы Продукции, цех КЦ2<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=166240974">Аттестация ЕП КЦ1,КЦ2 [2.2]</a>
 */
@Data
@SuperBuilder
@Jacksonized
public class CcmKc2Response {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO8601
    private Date ts;
    @NotNull
    private Pk pk; // Первичный ключ
    private Record data; // Данные

}
