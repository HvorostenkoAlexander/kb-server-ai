package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.entity.CcmMessage;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CcmMessageService {

    Optional<CcmMessage> save(CcmMessage ccmMessage);

    Page<CcmMessage> findAll(PageRequest of);

    CcmMessage update(CcmMessage ccmMessage);

    /**
     * Поиск последнего сообщения с запросом на Аттестацию
     *
     * @param primeId идентификатор Единицы Металла (Единицы Продукции)
     * @return найденное сообщение или пусто
     */
    Optional<CcmMessage> findLastMessage(String primeId);

    /**
     * Поиск исходного сообщения запроса на Аттестацию по идентификатору Запроса на Аттестацию
     */
    Optional<CcmMessageSourceDto> findSourceMessageByRequestId(Long requestId);

    /**
     * Поиск исходного сообщения запроса на Аттестацию по идентификатору Единицы Металла
     */
    Optional<CcmMessageSourceDto> findSourceMessageByPrimeId(String primeId);

    /**
     * Сохранить исходное сообщение
     *
     * @param requestId              requestId запроса ccm_message таблицы
     * @param primeId                primeId запроса ccm_message таблицы
     * @param ccmSourceMessageString строка исходное сообщение
     * @param createdAt              дата создания
     */
    void saveSourceMessage(Long requestId, String primeId, String ccmSourceMessageString, LocalDateTime createdAt);

}
