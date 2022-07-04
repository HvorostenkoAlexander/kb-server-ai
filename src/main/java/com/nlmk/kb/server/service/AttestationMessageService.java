package com.nlmk.kb.server.service;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;

/**
 * Обработка запроса на Аттестацию для заданных типов
 */
public interface AttestationMessageService {

    /**
     * Обработка запроса на Аттестацию для типа <code>CcmPtsRequest</code>
     *
     * @param request сообщения с запросом на Аттестацию
     * @return объект ответа тип <code>CcmPtsResponse</code>
     */
    CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest request);

}
