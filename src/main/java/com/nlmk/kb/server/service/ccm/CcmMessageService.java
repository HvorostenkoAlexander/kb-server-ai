package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.entity.CcmMessageSource;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CcmMessageService {

    Optional<CcmMessage> save(CcmMessage ccmMessage);

    Page<CcmMessage> findAll(PageRequest of);

    List<CcmMessage> findByPrimeId(String primeId);

    CcmMessage update(CcmMessage ccmMessage);

    /**
     * Поиск последнего сообщения с запросом на Аттестацию
     *
     * @param primeId идентификатор Единицы Металла (Единицы Продукции)
     * @return найденное сообщение или пусто
     */
    Optional<CcmMessage> findLastMessage(String primeId);

    /**
     * Поиск исходного сообщения по id запроса на аттестацию
     * @param requestId requestId запроса
     * @return {@link java.util.Optional} of {@link CcmMessageSource}
     */
    Optional<CcmMessageSourceDto> findSourceMessageByRequestId(Long requestId);

    /**
     * Сохранить исходное сообщение
     * @param requestId requestId запроса ccm_message таблицы
     * @param primeId primeId запроса ccm_message таблицы
     * @param ccmSourceMessageString строка исходное сообщение
     */
    void save(Long requestId, String primeId, String ccmSourceMessageString);

}
