package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.entity.AttestationMessage;

import java.util.List;
import java.util.Optional;

/**
 * Обработка запроса на Аттестацию для заданных типов
 */
public interface AttestationMessageService {

    /**
     * Поиск последнего сообщения с запросом на Аттестацию
     *
     * @param primeId идентификатор Единицы Металла (Единице Продукции)
     * @return найденное сообщение или пусто
     */
    Optional<AttestationMessage> findLastAttestationMessage(String primeId);

    /**
     * Обновление сообщения с запросом на Аттестацию
     *
     * @param attMessage объект сообщения
     */
    void updateAttestationMessage(AttestationMessage attMessage);

    /**
     * Обработка запроса на Аттестацию для типа <code>CcmPtsRequest</code>
     *
     * @param request сообщения с запросом на Аттестацию
     * @return объект ответа тип <code>CcmPtsResponse</code>
     */
    CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest request);

    /**
     * Поиск всех сообщений с запросами на Аттестацию
     *
     * @param primeId идентификатор Единицы Металла (Единице Продукции)
     * @return список сообщений или пустой список
     */
    List<AttestationRequest> findAllAttestationRequestByPrimeId(String primeId);

}
