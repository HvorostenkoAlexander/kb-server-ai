package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.api.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.entity.CcmMessageSource;
import com.nlmk.kb.server.exception.AttestationRequestNotFoundException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.ccm.CcmMessageSourceService;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.sap.S3Service;
import com.nlmk.kb.server.service.sender.PsmSender;
import io.micrometer.core.annotation.Timed;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsPts;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Timed(percentiles = {0.99, 0.95})
@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.POST, RequestMethod.GET})
public class KbControllerImpl implements KbController {

    private final CcmMessageService ccmMessageService;
    private final PdmMessageService pdmMessageService;
    private final CcmCommonService ccmCommonService;
    private final S3Service s3Service;
    private final PsmSender psmSender;
    private final AttestationResultSender attestationResultSender;
    private final AttestationMessageService attestationMessageService;
    private final CcmMessageSourceService ccmMessageSourceService;

    @Override
    public ResponseEntity<String> postLaunchReAttestation(String primeId) {
        log.info("postLaunchReAttestation, повторная отправка запроса на аттестацию из kb-server. primeId:[{}]", primeId);

        final var resultId = ccmCommonService.rePostAttestation(primeId);
        final var resultString = String.format(
                "Результат: [%s] получен при повторной отправки запроса на аттестацию с primeId: [%s]",
                resultId,
                primeId
        );

        return ResponseEntity.ok(resultString);
    }

    @Override
    public Page<CcmMessage> getAttestationRequestAllByPage(int page, int size) {
        log.info("getAttestationRequestAllByPage, page [{}], size [{}]", page, size);
        return ccmMessageService.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<CcmMessage> getCcmMessageByPrimeId(String primeId) {
        log.info("getCcmMessageByPrimeId, primeId [{}]", primeId);
        return ccmMessageService.findByPrimeId(primeId);
    }

    @Override
    public AttestationRequest getAttestationRequestForPrimeId(String primeId) {
        log.info("getAttestationRequestForPrimeId, primeId [{}]", primeId);

        // запросы на Аттестацию в двух разных таблицах
        final var ccmKafka = ccmMessageService.findLastMessage(primeId);
        final var ccmRest = attestationMessageService.findLastAttestationMessage(primeId);

        if (ccmKafka.isEmpty() && ccmRest.isEmpty()) {
            throw new AttestationRequestNotFoundException(MessageFormat.format(
                    "AttestationRequest for primeId [{0}] not found", primeId
            ));
        }

        if (ccmKafka.isPresent() && ccmRest.isPresent()) {
            // какое сообщение последнее?
            if (ccmKafka.get().getKbReceiptTs().after(ccmRest.get().getReceiptTs())) {
                return ccmKafka.get().getRequest();
            }
            return attestationMessageService.getAttestationRequestFromMessage(ccmRest.get());
        } else if (ccmRest.isPresent()) {
            return attestationMessageService.getAttestationRequestFromMessage(ccmRest.get());
        }

        return ccmKafka.get().getRequest();
    }

    @Override
    public Page<PdmMessageDto> getPdmTopicMessages(int page,
                                                   int size,
                                                   String topic,
                                                   Boolean isPosted,
                                                   Date startDate,
                                                   Date endDate) {
        log.info("getPdmTopicMessages, topic [{}], posted [{}], startDate [{}], endDate [{}]", topic, isPosted, startDate, endDate);
        return pdmMessageService.getMessages(topic,
                isPosted,
                startDate,
                endDate,
                PageRequest.of(page, size)
        );
    }

    @Override
    public List<PdmMessageDto> getPdmTopicMessagesByOffset(String topic, Integer partition, Long offset) {
        log.info("getPdmTopicMessagesByOffset, topic [{}], partition [{}], offset [{}]", topic, partition, offset);
        return pdmMessageService.getMessagesByOffset(topic, partition, offset);
    }

    @Override
    public PdmMessageDto getPdmMessageById(Long id) {
        log.info("getPdmMessageById, id [{}]", id);
        return pdmMessageService.getMessageById(id);
    }

    @Override
    public ResponseEntity<Long> postResendingPdmMessageById(Long id) {
        log.info("postResendingPdmMessageById, id [{}]", id);
        final var responseFromNsi = pdmMessageService.resendingToNsi(id);
        return new ResponseEntity<>(id, responseFromNsi.getStatusCode());
    }

    @Override
    public Long deletePdmMessageById(Long id) {
        log.info("deletePdmMessageById, id [{}]", id);
        return pdmMessageService.deleteMessageById(id);
    }

    @Override
    public ResponseEntity<String> postSendingSapMessage(String message) throws JsonProcessingException {
        log.info("postSendingSapMessage, message [{}]", message);

        ZORDERS051 zorder = s3Service.getZorder(message);

        psmSender.postZorder(zorder);
        log.info("kb, sendingSapMessage. Sent to PSM BELNR: [{}]", zorder.getIDOC().getE1EDK01().getBELNR());

        return new ResponseEntity<>("BELNR: " + zorder.getIDOC().getE1EDK01().getBELNR(), HttpStatus.OK);
    }

    @Override
    public void postProductAttestationResult(ProductAttestationResultDto attestationResult) {
        log.info("postProductAttestationResult, ProductAttestationResultDto [{}]", attestationResult);
        switch (attestationResult.getKceh()) {
            case PGP: {
                attestationResultSender.send(attestationResult, VerificationResults.class);
                break;
            }
            case PTS: {
                attestationResultSender.send(attestationResult, VerificationResultsPts.class);
                break;
            }
            default: throw new AttestationResultSenderException("Wrong Kceh Value for send result");
        }
    }

    @Override
    public ResponseEntity<String> getCcmSourceMessage(Long orderNum) {
        log.info("getCcmSourceMessage, orderNum [{}]", orderNum);
        return ResponseEntity.ok(ccmMessageSourceService.findByRequestId(orderNum)
                .map(CcmMessageSource::getMessageSource)
                .orElse("Not found"));
    }

}
