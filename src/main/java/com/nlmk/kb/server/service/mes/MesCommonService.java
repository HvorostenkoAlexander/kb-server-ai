package com.nlmk.kb.server.service.mes;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.MesMessage;

import java.util.Optional;

public interface MesCommonService {

    /**
     * Отправка сохраненного сообщения в сервис Аттестации
     *
     * @param mesMessage преобразованное сообщение Kafka с запросом на Аттестацию
     * @return объект ответа сервиса Аттестации
     */
    Optional<ProductAttestationResultDto> postAttestation(MesMessage mesMessage);

}
