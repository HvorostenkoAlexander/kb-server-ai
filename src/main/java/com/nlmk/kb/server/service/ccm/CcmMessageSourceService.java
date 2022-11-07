package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.CcmMessageSource;
import java.util.Optional;

public interface CcmMessageSourceService {

    /**
     * Поиск исходного сообщения по orderNum
     * @param requestId orderNum запроса
     * @return {@link java.util.Optional} of {@link CcmMessageSource}
     */
    Optional<CcmMessageSource> findByRequestId(Long requestId);

    /**
     * Сохранить исходное сообщение
     * @param requestId orderNum запроса ccm_message таблицы
     * @param ccmMessageSourceString строка исходное сообщение
     */
    void save(Long requestId, String ccmMessageSourceString);
}
