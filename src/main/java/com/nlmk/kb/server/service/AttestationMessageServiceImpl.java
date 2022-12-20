package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.AttestationMessageSender;
import com.nlmk.kb.server.exception.CcmRequestParsingException;
import com.nlmk.kb.server.exception.CcmRequestProcessingException;
import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.sender.PamSender;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AttestationMessageServiceImpl implements AttestationMessageService {

    private final RestRequestAdapter<CcmPtsRequest> ccmPtsRestRequestAdapter;
    private final RestResponseAdapter<CcmPtsResponse> ccmPtsRestResponseAdapter;

    private final AttestationMessageRepository repository;
    private final PamSender pamSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<AttestationMessage> findLastAttestationMessage(String primeId) {
        return repository.findFirstByPrimeIdOrderByReceiptTsDesc(primeId);
    }

    @Override
    @Transactional
    public void updateAttestationMessage(AttestationMessage attMessage) {
        repository.save(attMessage);
    }

    @Override
    @Transactional
    public CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest request) {
        /*
         * 1. принять запрос на аттестацию
         * 2. преобразовать запрос к общему виду для PAM (com.nlmk.attestation.product.api.pam.AttestationRequest)
         * 3. отправить запрос на аттестацию в PAM
         * 5. получить ответ от PAM
         * 3. сохранить запрос в базу (для запуска повторной аттестации по сообщениям САДиМ)
         * 6. преобразовать и отправить ответ
         */
        log.info("ccmPtsRequestProcessing, request [{}]", request);
        final var attRequest = ccmPtsRestRequestAdapter.adapt(request);
        final var primeId = SenderUtils.getPrimeId(attRequest);

        try {
            final var attMessage = AttestationMessage.builder()
                    .sender(AttestationMessageSender.CCM_PTS)
                    .receiptTs(new Date())
                    .primeId(primeId)
                    .request(objectMapper.writeValueAsString(attRequest))
                    .build();

            final var attResult = pamSender.postAttestationRequest(attRequest);
            attMessage.setAttestationTs(new Date());
            repository.save(attMessage);
            return ccmPtsRestResponseAdapter.adapt(attResult);
        } catch (JsonProcessingException e) {
            log.error("ccmPtsRequestProcessing", e);
            throw new CcmRequestProcessingException(
                    MessageFormat.format("ccmPtsRequestProcessing, error for primeId [{0}]", primeId)
            );
        }
    }

    @Override
    public AttestationRequest getAttestationRequestFromMessage(AttestationMessage message) {
        if (Objects.isNull(message)) {
            return null;
        }

        try {
            return objectMapper.readValue(message.getRequest(), AttestationRequest.class);
        } catch (JsonProcessingException e) {
            log.error("getAttestationRequestFromMessage", e);
            throw new CcmRequestParsingException(MessageFormat.format(
                    "getAttestationRequestFromMessage, parsing error for primeId [{0}]", message.getPrimeId()
            ));
        }
    }

}
