package com.nlmk.kb.server.service.impl;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import com.nlmk.kb.server.service.CcmCommonService;
import com.nlmk.kb.server.service.CcmMessageService;
import com.nlmk.kb.server.service.CcmPamClientSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmCommonServiceImpl implements CcmCommonService {

    private final CcmPamClientSender ccmPamSender;
    private final CcmMessageService messageService;

    @Override
    public Long rePostAttestation(String primeId) throws IllegalArgumentException {
        Long resultId = null;

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

        try {
            final var lastRequest = requests.stream()
                    .max(
                            Comparator.comparing(CcmAttestationRequestMessage::getKbReceiptTs)
                    ).orElseThrow(
                            () -> new IllegalArgumentException("Не удалось получить сведения о последнем запросе" +
                                    " с primeId: " + primeId)
                    );

            resultId = rePostRequest(lastRequest);
        } catch (Exception ex) {
            log.error("Ошибка при повторной передачи запроса на аттестацию: [{}]", ex.getMessage());
        }
        return resultId;
    }

    @Override
    public Long postAttestation(CcmAttestationRequestMessage request) {
        Assert.notNull(request, "request is null");

        Long resultId = null;

        final var savedRequest = messageService.save(request).orElseThrow(
                () -> new RuntimeException("Не удалось сохранить сообщение partition: " + request.getPartition()
                        + "; offset: " + request.getOffset())
        );

        if (request.getRequest() != null ||
                request.getRequest().getValue() != null ||
                request.getRequest().getValue().getData() != null) {

            resultId = postRequest(savedRequest, "recived");

        } else {
            log.warn("В поступившем запросе на аттестацию нет данных. Отправка невозможна.");
        }
        return resultId;
    }

    private Long rePostRequest(CcmAttestationRequestMessage r) {
        log.info("Повторная отправка запроса на аттестацию. primeId: [{}], kbReceiptTs:[{}]",
                r.getPrimeId(), r.getKbReceiptTs());

        return postRequest(r, "re-recived");
    }

    private Long postRequest(CcmAttestationRequestMessage r, String statusNote) {

        final var pamResult = ccmPamSender.postAttestationRequest(r.getRequest());

        if (pamResult != null) {
            r.setStatus(statusNote);
            r.setKbSendingTs(new Date());
            messageService.update(r);
        }
        return pamResult;
    }
}
