package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.AttestationMessage;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.AttestationMessageService;
import com.nlmk.kb.server.service.sender.PamSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmCommonServiceImpl implements CcmCommonService {

    private final PamSender pamSender;
    private final CcmMessageService ccmMessageService;
    private final AttestationMessageService attMessageService;

    @Override
    public Optional<ProductAttestationResultDto> rePostAttestation(String primeId) throws IllegalArgumentException {
        if (StringUtils.isBlank(primeId)) {
            log.warn("Невозможно осуществить повторную отправку. primeId is null.");
            throw new IllegalArgumentException("Невозможно осуществить повторную отправку. primeId is null.");
        }

        // поиск сообщений двух типов: CcmMessage и AttestationMessage (выборка последнего из найденного!)

        final var lastCcmMessage = findLastCcmMessage(primeId);
        final var lastAttMessage = findLastAttestationMessage(primeId);

        if (lastCcmMessage.isEmpty()
                && lastAttMessage.isEmpty()) {
            log.warn("rePostAttestation, для повторной отправки в базе данных kb-server не обнаружены запросы на аттестацию с primeId: [{}]", primeId);
            throw new IllegalArgumentException(String.format("Для повторной отправки в базе данных kb-server не обнаружены запросы на аттестацию с primeId: [%s]", primeId));
        }

        if (lastCcmMessage.isPresent() && lastAttMessage.isPresent()) {
            // какое сообщение последнее?
            if (lastCcmMessage.get().getKbReceiptTs()
                    .after(lastAttMessage.get().getReceiptTs())) {
                // CcmMessage последнее
                return Optional.of(rePostCcmMessage(lastCcmMessage.get()));
            }
            return Optional.of(rePostAttestationMessage(lastAttMessage.get()));
        } else if (lastCcmMessage.isPresent()) {
            return Optional.of(rePostCcmMessage(lastCcmMessage.get()));
        }

        return Optional.of(rePostAttestationMessage(lastAttMessage.get()));
    }

    /**
     * Поиск и выбор последнего сообщения с запросом на Аттестацию (CcmMessage)
     *
     * @param primeId идентификатор ЕМ
     * @return найденное сообщение или пусто
     */
    private Optional<CcmMessage> findLastCcmMessage(String primeId) {
        return ccmMessageService.findLastMessage(primeId)
                .or(() -> {
                    log.info("findLastCcmMessage, last CcmMessage not found by primeId: [{}]", primeId);
                    return Optional.empty();
                });
    }

    /**
     * Поиск и выбор последнего сообщения с запросом на Аттестацию (AttestationMessage)
     *
     * @param primeId идентификатор ЕМ
     * @return найденное сообщение или пусто
     */
    private Optional<AttestationMessage> findLastAttestationMessage(String primeId) {
        return attMessageService.findLastAttestationMessage(primeId)
                .or(() -> {
                    log.info("findLastAttestationMessage, last AttestationMessage not found by primeId: [{}]", primeId);
                    return Optional.empty();
                });
    }

    @Override
    public Optional<ProductAttestationResultDto> postAttestation(CcmMessage ccmMessage) {
        Assert.notNull(ccmMessage, "request is null");

        final var savedRequest = ccmMessageService.save(ccmMessage).orElseThrow(
                () -> new RuntimeException("Не удалось сохранить сообщение partition: " + ccmMessage.getPartition()
                        + "; offset: " + ccmMessage.getOffset())
        );

        if (ccmMessage.getRequest() == null
                || ccmMessage.getRequest().getValue() == null
                || ccmMessage.getRequest().getValue().getData() == null) {
            log.warn("В поступившем запросе на аттестацию нет данных. Отправка невозможна.");
            return Optional.empty();
        }

        return Optional.of(postCcmMessage(savedRequest, "recived"));
    }

    private ProductAttestationResultDto rePostCcmMessage(CcmMessage ccmMessage) {
        log.info("Повторная отправка запроса на аттестацию. id:[{}]; primeId: [{}]; kbReceiptTs:[{}]",
                ccmMessage.getId(), ccmMessage.getPrimeId(), ccmMessage.getKbReceiptTs());

        return postCcmMessage(ccmMessage, "re-recived");
    }

    private ProductAttestationResultDto postCcmMessage(CcmMessage ccmMessage, String statusNote) {

        final var pamResult = pamSender.postAttestationRequest(ccmMessage.getRequest());

        if (pamResult != null) {
            ccmMessage.setStatus(statusNote);
            ccmMessage.setKbSendingTs(new Date());
            ccmMessageService.update(ccmMessage);
        }
        return pamResult;
    }

    private ProductAttestationResultDto rePostAttestationMessage(AttestationMessage attMessage) {
        log.info("Повторная отправка запроса на аттестацию. id:[{}]; primeId: [{}]; receiptTs:[{}]",
                attMessage.getId(), attMessage.getPrimeId(), attMessage.getReceiptTs());

        final var attRequest = attMessageService.getAttestationRequestFromMessage(attMessage);
        final var pamResult = pamSender.postAttestationRequest(attRequest);
        if (pamResult != null) {
            attMessage.setAttestationTs(new Date());
            attMessageService.updateAttestationMessage(attMessage);
        }
        return pamResult;
    }

}
