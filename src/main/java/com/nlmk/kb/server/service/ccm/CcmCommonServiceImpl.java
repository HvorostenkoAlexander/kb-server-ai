package com.nlmk.kb.server.service.ccm;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.sender.PamSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmCommonServiceImpl implements CcmCommonService {

    private final PamSender ccmPamSender;
    private final CcmMessageService messageService;

    @Override
    public Optional<ProductAttestationResultDto> rePostAttestation(String primeId) throws IllegalArgumentException {

        // добавить поиск сообщений двух типов: CcmMessage и AttestationMessage todo
        // (выборка самого последнего из всех!)

        if (StringUtils.isBlank(primeId)) {
            log.warn("Невозможно осуществить повторную отправку. primeId is null.");
            throw new IllegalArgumentException("Невозможно осуществить повторную отправку. primeId is null.");
        }
        final var requests = messageService.findByPrimeId(primeId);

        if (requests.isEmpty()) {
            log.debug("Для повторной отправки в базе данных kb-server не обнаружены запросы на аттестацию с primeId: [{}]", primeId);
            throw new IllegalArgumentException("Для повторной отправки в базе данных kb-server не " +
                    "обнаружены запросы на аттестацию с primeId: " + primeId);
        }

        if (requests.size() > 1) {
            log.warn("В базе данных kb-server обнаружено [{}] запроса на аттестацию с primeId: [{}]",
                    requests.size(), primeId);

            requests.stream().map(
                    r -> "id: " + r.getId() + " kbReceiptTs: " + r.getKbReceiptTs() + "; primeId: " + r.getPrimeId()
            ).forEach(log::debug);
        }

        final var lastRequest = requests.stream()
                .max(
                        Comparator.comparing(CcmMessage::getKbReceiptTs)
                ).orElseThrow(
                        () -> new IllegalArgumentException("Не удалось получить сведения о последнем запросе" +
                                " с primeId: " + primeId)
                );

        return Optional.of(rePostRequest(lastRequest));
    }

    @Override
    public Optional<ProductAttestationResultDto> postAttestation(CcmMessage ccmMessage) {
        Assert.notNull(ccmMessage, "request is null");

        final var savedRequest = messageService.save(ccmMessage).orElseThrow(
                () -> new RuntimeException("Не удалось сохранить сообщение partition: " + ccmMessage.getPartition()
                        + "; offset: " + ccmMessage.getOffset())
        );

        if (ccmMessage.getRequest() == null
                || ccmMessage.getRequest().getValue() == null
                || ccmMessage.getRequest().getValue().getData() == null) {
            log.warn("В поступившем запросе на аттестацию нет данных. Отправка невозможна.");
            return Optional.empty();
        }

        return Optional.of(postRequest(savedRequest, "recived"));
    }

    private ProductAttestationResultDto rePostRequest(CcmMessage r) {
        log.info("Повторная отправка запроса на аттестацию. id:[{}]; primeId: [{}]; kbReceiptTs:[{}]",
                r.getId(), r.getPrimeId(), r.getKbReceiptTs());

        return postRequest(r, "re-recived");
    }

    private ProductAttestationResultDto postRequest(CcmMessage r, String statusNote) {

        final var pamResult = ccmPamSender.postAttestationRequest(r.getRequest());

        if (pamResult != null) {
            r.setStatus(statusNote);
            r.setKbSendingTs(new Date());
            messageService.update(r);
        }
        return pamResult;
    }

}
