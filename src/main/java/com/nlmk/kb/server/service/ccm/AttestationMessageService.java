package com.nlmk.kb.server.service.ccm;

/**
 * Обработка запроса на Аттестацию для заданных типов
 *
 * @param <I> входной тип сообщения
 * @param <O> выходной тип ответа
 */
public interface AttestationMessageService<I, O> {

    /**
     * Обработка запроса на Аттестацию
     *
     * @param attRequest сообщения с запросом на Аттестацию, заданного типа
     * @return объект ответа, заданного типа
     */
    O requestProcessing(I attRequest);

}
