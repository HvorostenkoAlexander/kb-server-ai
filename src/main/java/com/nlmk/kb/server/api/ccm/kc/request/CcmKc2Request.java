package com.nlmk.kb.server.api.ccm.kc.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.nlmk.kb.server.api.ccm.kc.Pk;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на Аттестацию Единицы Продукции, цех КЦ2<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=166240974">Аттестация ЕП КЦ1,КЦ2 [2.1]</a>
 */
@Data
@Builder
@Jacksonized
public class CcmKc2Request {

    private final @NotBlank String ts; // Дата и время передачи
    private final @NotNull @Valid Pk pk; // Первичный ключ
    private final @Valid Record data; // Данные

}
