package com.nlmk.kb.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.api.ccm.kc.CcmKc1Request;
import com.nlmk.kb.server.api.ccm.kc.CcmKc1Response;
import com.nlmk.kb.server.api.ccm.kc.CcmKc2Request;
import com.nlmk.kb.server.api.ccm.kc.CcmKc2Response;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsRequest;
import com.nlmk.kb.server.api.ccm.pts.CcmPtsResponse;
import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.AttestationMessageSender;
import com.nlmk.kb.server.entity.CcmMessageSource;
import com.nlmk.kb.server.exception.CcmRequestParsingException;
import com.nlmk.kb.server.exception.CcmRequestProcessingException;
import com.nlmk.kb.server.repository.AttestationMessageRepository;
import com.nlmk.kb.server.repository.CcmMessageSourceRepository;
import com.nlmk.kb.server.service.ccm.RestRequestAdapter;
import com.nlmk.kb.server.service.ccm.RestResponseAdapter;
import com.nlmk.kb.server.service.sender.PamSender;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AttestationMessageServiceImpl implements AttestationMessageService {

    private final RestRequestAdapter<CcmPtsRequest> ccmPtsRestRequestAdapter;
    private final RestResponseAdapter<CcmPtsResponse> ccmPtsRestResponseAdapter;
    private final RestRequestAdapter<CcmKc1Request> ccmKc1RestRequestAdapter;
    private final RestResponseAdapter<CcmKc1Response> ccmKc1RestResponseAdapter;
    private final RestRequestAdapter<CcmKc2Request> ccmKc2RestRequestAdapter;
    private final RestResponseAdapter<CcmKc2Response> ccmKc2RestResponseAdapter;
    private final AttestationMessageRepository attestationMessageRepository;
    private final CcmMessageSourceRepository ccmMessageSourceRepository;
    private final PamSender pamSender;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @FunctionalInterface
    interface Saver {
        void save(Long requestId, String primeId, LocalDateTime createdAt) throws JsonProcessingException;
    }
    class SourceMessageSaver<T> implements Saver {
        private final CcmMessageSourceRepository sourceRepository;
        private final T sourceMessage;
        public SourceMessageSaver(CcmMessageSourceRepository sourceRepository, T t) {
            this.sourceRepository = sourceRepository;
            this.sourceMessage = t;
        }

        @Override
        public void save(Long requestId, String primeId, LocalDateTime createdAt) throws JsonProcessingException {
            this.sourceRepository.save(
                    CcmMessageSource.builder()
                            .requestId(requestId)
                            .primeId(primeId)
                            .createdAt(createdAt)
                            .messageSource(objectMapper.writeValueAsString(this.sourceMessage))
                            .build()
            );
        }
    }

    @Override
    public Optional<AttestationMessage> findLastAttestationMessage(String primeId) {
        return attestationMessageRepository.findFirstByPrimeIdOrderByReceiptTsDesc(primeId);
    }

    @Override
    @Transactional
    public void updateAttestationMessage(AttestationMessage attMessage) {
        attestationMessageRepository.save(attMessage);
    }

    @Override
    @Transactional
    public CcmPtsResponse ccmPtsRequestProcessing(CcmPtsRequest request) {
        log.info("ccmPtsRequestProcessing, request [{}]", request);
        final var attRequest = ccmPtsRestRequestAdapter.adapt(request);
        return ccmPtsRestResponseAdapter.adapt(processAttestationRequest(attRequest,
                AttestationMessageSender.CCM_PTS,
                new SourceMessageSaver<>(ccmMessageSourceRepository, request))
        );
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

    @Override
    @Transactional
    public CcmKc1Response ccmKc1RequestProcessing(CcmKc1Request request) {
        log.info("ccKc1RequestProcessing, request [{}]", request);
        final var attRequest = ccmKc1RestRequestAdapter.adapt(request);
        return ccmKc1RestResponseAdapter.adapt(processAttestationRequest(attRequest,
                AttestationMessageSender.CCM_KC1,
                new SourceMessageSaver<>(ccmMessageSourceRepository, request))
        );
    }

    @Override
    @Transactional
    public CcmKc2Response ccmKc2RequestProcessing(CcmKc2Request request) {
        log.info("ccKc2RequestProcessing, request [{}]", request);
        final var attRequest = ccmKc2RestRequestAdapter.adapt(request);
        return ccmKc2RestResponseAdapter.adapt(processAttestationRequest(attRequest,
                AttestationMessageSender.CCM_KC2,
                new SourceMessageSaver<>(ccmMessageSourceRepository, request))
        );
    }


    private ProductAttestationResultDto processAttestationRequest(AttestationRequest attestationRequest,
                                                                  AttestationMessageSender sender,
                                                                  Saver sourceSaver) {
        /*
         * 1. принять запрос на аттестацию
         * 2. преобразовать запрос к общему виду для PAM (com.nlmk.attestation.product.api.pam.AttestationRequest)
         * 3. отправить запрос на аттестацию в PAM
         * 4. получить ответ от PAM
         * 5. сохранить первоисточник запроса в базу (для просмотра запроса в UI)
         * 6. сохранить запрос в базу (для запуска повторной аттестации по сообщениям САДиМ)
         * 7. преобразовать и отправить ответ
         */
        final var primeId = SenderUtils.getPrimeId(attestationRequest);

        try {
            final var attMessage = AttestationMessage.builder()
                    .sender(sender)
                    .receiptTs(new Date())
                    .primeId(primeId)
                    .request(objectMapper.writeValueAsString(attestationRequest))
                    .build();

            final var attResult = pamSender.postAttestationRequest(attestationRequest);

            if (Objects.nonNull(attResult)
                    && Objects.nonNull(attResult.getResult())
                    && !CollectionUtils.isEmpty(attResult.getResult().getRequests())) {
                final var requestId = attResult.getResult().getRequests().get(0).getId(); // результат аттестации содержит один экземпляр запроса, т.е. индекс = 0.
                if (Objects.nonNull(requestId)) {
                    sourceSaver.save(requestId, primeId, LocalDateTime.now());
                }
            }

            attMessage.setAttestationTs(new Date());
            attestationMessageRepository.save(attMessage);
            return attResult;
        } catch (JsonProcessingException e) {
            log.error("processAttestationRequest", e);
            throw new CcmRequestProcessingException(
                    MessageFormat.format("processAttestationRequest, error for primeId [{0}]", primeId)
            );
        }

    }
}
