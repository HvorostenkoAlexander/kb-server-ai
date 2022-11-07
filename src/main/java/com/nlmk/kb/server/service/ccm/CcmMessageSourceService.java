package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.CcmMessageSource;
import java.util.List;

public interface CcmMessageSourceService {

    /**
     * Поиск исходного сообщения по orderNum
     * @param requestId orderNum запроса
     * @return строка исходного сообщения
     */
    List<CcmMessageSource> findByRequestId(Long requestId);

    /**
     * Сохранить исходное сообщение
     * @param requestId orderNum запроса ccm_message таблицы
     * @param ccmMessageSourceString строка исходное сообщение
     */
    void save(Long requestId, String ccmMessageSourceString);
}
