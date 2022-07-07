package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.CcmMessage;

import java.util.Optional;

public interface CcmCommonService {

    /**
     * Повторная отправка запроса на Аттестацию, после получения сообщения САДиМ<br>
     * Поиск производится в <b>двух</b> таблицах: CcmMessage и AttestationMessage<br>
     * В таблицах сообщения от разных систем, поиск по <code>primeId</code>
     *
     * @param primeId идентификатор Единицы Металла (Единице Продукции)
     * @return объект ответа сервиса Аттестации
     * @throws IllegalArgumentException исключение при ошибке поиска сообщения
     */
    Optional<ProductAttestationResultDto> rePostAttestation(String primeId) throws IllegalArgumentException;

    /**
     * Отправка сохраненного сообщения в сервис Аттестации
     *
     * @param ccmMessage преобразованное сообщение Kafka с запросом на Аттестацию
     * @return объект ответа сервиса Аттестации
     */
    Optional<ProductAttestationResultDto> postAttestation(CcmMessage ccmMessage);

}
