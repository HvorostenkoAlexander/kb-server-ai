package com.nlmk.kb.server.api.ccm.pts;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Ответ на Аттестацию Единицы Продукции, цех ЦТС<br>
 * Ссылка <a href="https://confluence.nlmk.com/pages/viewpage.action?pageId=120034208">Аттестация ЕП ЦТС</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcmPtsResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO8601
    private Date ts;

}
