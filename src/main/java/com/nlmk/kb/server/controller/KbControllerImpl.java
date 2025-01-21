package com.nlmk.kb.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nlmk.attestation.product.api.kb.SapMessageDto;
import com.nlmk.attestation.product.api.pam.AttestationRequest;
import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.api.CcmMessageSourceDto;
import com.nlmk.kb.server.api.PdmMessageDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.exception.AttestationResultSenderException;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.ccm.CcmCommonService;
import com.nlmk.kb.server.service.ccm.CcmMessageService;
import com.nlmk.kb.server.service.pdm.PdmMessageService;
import com.nlmk.kb.server.service.result.sending.AttestationResultSender;
import com.nlmk.kb.server.service.sap.S3Service;
import com.nlmk.kb.server.service.sap.SapMessageService;
import com.nlmk.kb.server.service.sender.PsmSender;
import io.micrometer.core.annotation.Timed;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import nlmk.l3.apcs.VerificationResultsKc1;
import nlmk.l3.apcs.VerificationResultsKc2;
import nlmk.l3.apcs.VerificationResultsPhpp;
import nlmk.l3.apcs.VerificationResultsPts;
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
    private final SapMessageService sapMessageService;
    private final S3Service s3Service;
    private final PsmSender psmSender;
    private final AttestationResultSender attestationResultSender;
    private final AttestationMessageService attestationMessageService;

    @Override
    public ResponseEntity<String> postLaunchReAttestation(String primeId) {
        log.info("postLaunchReAttestation, повторная отправка запроса на аттестацию из kb-server. primeId:[{}]", primeId);

        final var resultId = ccmCommonService.rePostAttestation(primeId);
        final var resultString = String.format("Результат: [%s] получен при повторной отправки запроса на аттестацию с primeId: [%s]", resultId, primeId);

        return ResponseEntity.ok(resultString);
    }

    @Override
    public Page<CcmMessage> getAttestationRequestAllByPage(int page, int size) {
        log.info("getAttestationRequestAllByPage, page [{}], size [{}]", page, size);
        return ccmMessageService.findAll(PageRequest.of(page, size));
    }

    public AttestationRequest getAttestationRequestForPrimeId(String primeId) {
        log.info("getAttestationRequestForPrimeId, primeId [{}]", primeId);

        // запросы на Аттестацию в двух разных таблицах
        final var ccmKafka = ccmMessageService.findLastMessage(primeId);
        final var ccmRest = attestationMessageService.findLastAttestationMessage(primeId);

        if (ccmKafka.isEmpty() && ccmRest.isEmpty()) {
            throw new DataNotFoundException(MessageFormat.format("Запрос аттестации с primeId [{0}] не найден", primeId));
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
    public Page<PdmMessageDto> getPdmTopicMessages(int page, int size, String topic, Boolean isPosted, Date startDate, Date endDate) {
        log.info("getPdmTopicMessages, topic [{}], posted [{}], startDate [{}], endDate [{}]", topic, isPosted, startDate, endDate);
        return pdmMessageService.getMessages(topic, isPosted, startDate, endDate, PageRequest.of(page, size));
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
    public ResponseEntity<String> postSendingSapZorderMessage(String message) throws JsonProcessingException {
        log.info("postSendingSapZorderMessage, message [{}]", message);

        ZORDERS051 zorder = s3Service.unmarshalZorder(message);

        psmSender.postZorder(zorder);
        log.info("postSendingSapZorderMessage, в PSM отправлен заказ BELNR: [{}]", zorder.getIDOC().getE1EDK01().getBELNR());

        return new ResponseEntity<>("BELNR: " + zorder.getIDOC().getE1EDK01().getBELNR(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> postSendingSapZmmorderMessage(String message) throws JsonProcessingException {
        log.info("postSendingSapZmmorderMessage, message [{}]", message);

        ZMMORDERS05DOP zmmorder = s3Service.unmarshalZmmorder(message);

        psmSender.postZmmorder(zmmorder);
        log.info("postSendingSapZmmorderMessage, в PSM отправлен заказ BELNR: [{}]", zmmorder.getIDOC().getE1EDK01().getBELNR());

        return new ResponseEntity<>("BELNR: " + zmmorder.getIDOC().getE1EDK01().getBELNR(), HttpStatus.OK);
    }

    @Override
    public SapMessageDto<?> getSapMessageNextId(Long id) {
        log.info("postSapMessageNextId, id [{}]", id);
        return sapMessageService.getNextSapMessage(id);
    }

    @Override
    public void postProductAttestationResult(ProductAttestationResultDto attestationResult) {
        log.info("postProductAttestationResult, ProductAttestationResultDto [{}]", attestationResult);
        switch (attestationResult.getKceh()) {
            case PGP:
                attestationResultSender.send(attestationResult, VerificationResults.class);
                break;
            case PTS:
                attestationResultSender.send(attestationResult, VerificationResultsPts.class);
                break;
            case KC1:
                attestationResultSender.send(attestationResult, VerificationResultsKc1.class);
                break;
            case KC2:
                attestationResultSender.send(attestationResult, VerificationResultsKc2.class);
                break;
            case PHPP:
                attestationResultSender.send(attestationResult, VerificationResultsPhpp.class);
                break;
            default:
                throw new AttestationResultSenderException("Отправка результата аттестации для цеха ["
                        + attestationResult.getKceh() + "] не реализована");
        }
    }

    @Override
    public ResponseEntity<CcmMessageSourceDto> getSourceRequestByPrimeId(String primeId) {
        log.info("getSourceRequestByPrimeId, primeId [{}]", primeId);
        return ResponseEntity.ok(ccmMessageService.findSourceMessageByPrimeId(primeId).orElse(new CcmMessageSourceDto()));
    }

    @Override
    public ResponseEntity<CcmMessageSourceDto> getSourceRequestByRequestId(Long requestId) {
        log.info("getCcmSourceMessage, requestId [{}]", requestId);
        return ResponseEntity.ok(ccmMessageService.findSourceMessageByRequestId(requestId).orElse(new CcmMessageSourceDto()));
    }

}
