package com.nlmk.kb.server.service;

import com.nlmk.kb.server.api.CcmPtsRequest;
import com.nlmk.kb.server.api.CcmPtsResponse;
import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.service.sender.PamSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttestationMessageServiceImpl implements AttestationMessageService {

    private final AttestationMessageRepository repository;
    private final PamSender pamSender;

    @Override
    public CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest attRequest) {
        // todo
        return null;
    }

    /*
     * 1. принять запрос на аттестацию
     * 2. преобразовать запрос к общему виду для PAM (com.nlmk.attestation.product.api.pam.AttestationRequest)
     * 3. сохранить в базу (для запуска повторной аттестации по сообщениям САДиМ)
     * 4. отправить запрос в PAM
     * 5. получить ответ
     * 6. преобразовать ответ и отправить
     */

}
