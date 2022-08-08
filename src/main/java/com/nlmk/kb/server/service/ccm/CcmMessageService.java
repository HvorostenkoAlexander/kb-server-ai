package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.entity.CcmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CcmMessageService {

    Optional<CcmMessage> save(CcmMessage ccmMessage);

    Page<CcmMessage> findAll(PageRequest of);

    List<CcmMessage> findByPrimeId(String primeId);

    CcmMessage update(CcmMessage ccmMessage);

    /**
     * Поиск последнего сообщения с запросом на Аттестацию
     *
     * @param primeId идентификатор Единицы Металла (Единице Продукции)
     * @return найденное сообщение или пусто
     */
    Optional<CcmMessage> findLastMessage(String primeId);

}
