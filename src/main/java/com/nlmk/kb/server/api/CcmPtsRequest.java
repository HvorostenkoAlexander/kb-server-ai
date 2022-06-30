package com.nlmk.kb.server.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

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
    private String ts;

}
