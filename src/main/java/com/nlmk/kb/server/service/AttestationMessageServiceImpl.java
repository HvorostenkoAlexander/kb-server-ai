package com.nlmk.kb.server.service;

import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.sender.PamSender;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AttestationMessageServiceImpl implements AttestationMessageService {

    /*
     * 1. принять запрос на аттестацию
     * 2. преобразовать запрос к общему виду для PAM (com.nlmk.attestation.product.api.pam.AttestationRequest)
     * 3. сохранить в базу (для запуска повторной аттестации по сообщениям САДиМ)
     * 4. отправить запрос в PAM
     * 5. получить ответ
     * 6. преобразовать ответ и отправить
     */

    private final RestRequestAdapter<CcmPtsRequest> ccmPtsRestRequestAdapter;
    private final RestResponseAdapter<CcmPtsResponse> ccmPtsRestResponseAdapter;

    private final AttestationMessageRepository repository;
    private final PamSender pamSender;

    @Override
    public CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest request) {
        log.info("ccmPtsRequestProcessing, request [{}]", request);
        final var attMessage = ccmPtsRestRequestAdapter.adapt(request);
        repository.save(attMessage);
        final var attResult = pamSender.postAttestationRequest(attMessage.getRequestObject());
        return ccmPtsRestResponseAdapter.adapt(attResult);
    }


}
