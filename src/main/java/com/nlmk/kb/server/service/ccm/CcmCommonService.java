package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.CcmMessage;

import java.util.Optional;

public interface CcmCommonService {

    Optional<ProductAttestationResultDto> rePostAttestation(String primeId) throws IllegalArgumentException;

    /**
     * Отправка сохраненного сообщения в сервис Аттестации
     *
     * @param ccmMessage преобразованное сообщение Kafka с запросом на Аттестацию
     * @return объект ответа сервиса Аттестации
     */
    Optional<ProductAttestationResultDto> postAttestation(CcmMessage ccmMessage);

}
