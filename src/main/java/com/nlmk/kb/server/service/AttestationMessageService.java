package com.nlmk.kb.server.service;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc1Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc1Response;
import com.nlmk.kb.server.api.ccm.kc.request.CcmKc2Request;
import com.nlmk.kb.server.api.ccm.kc.response.CcmKc2Response;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppRequest;
import com.nlmk.kb.server.api.ccm.phpp.CcmPhppResponse;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.entity.AttestationMessage;

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
     * Получение объекта AttestationRequest из сообщения
     *
     * @param message сообщение с запросом на Аттестацию
     * @return объект AttestationRequest или исключение
     */
    AttestationRequest getAttestationRequestFromMessage(AttestationMessage message);

    /**
     * Обработка запроса на Аттестацию для типа <code>CcmKc1Request</code>
     *
     * @param request сообщения с запросом на Аттестацию
     * @return объект ответа тип <code>CcmKc1Response</code>
     */
    CcmKc1Response ccmKc1RequestProcessing(CcmKc1Request request);

    /**
     * Обработка запроса на Аттестацию для типа <code>CcmKc2Request</code>
     *
     * @param request сообщения с запросом на Аттестацию
     * @return объект ответа тип <code>CcmKc2Response</code>
     */
    CcmKc2Response ccmKc2RequestProcessing(CcmKc2Request request);

    /**
     * Обработка запроса на Аттестацию для типа <code>CcmPhppRequest</code>
     *
     * @param request сообщения с запросом на Аттестацию
     * @return объект ответа тип <code>CcmPhppResponse</code>
     */
    CcmPhppResponse ccmPhppRequestProcessing(CcmPhppRequest request);
}
