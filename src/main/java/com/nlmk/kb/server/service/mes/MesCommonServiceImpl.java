package com.nlmk.kb.server.service.mes;

import com.nlmk.attestation.product.api.pam.ProductAttestationResultDto;
import com.nlmk.kb.server.entity.MesMessage;
import com.nlmk.kb.server.service.sender.PamSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MesCommonServiceImpl implements MesCommonService {

    private final PamSender pamSender;
    private final MesMessageService mesMessageService;
    //private final AttestationMessageService attMessageService;

    @Override
    public Optional<ProductAttestationResultDto> postAttestation(MesMessage mesMessage) {
        Assert.notNull(mesMessage, "request is null");

        final var savedRequest = mesMessageService.save(mesMessage).orElseThrow(
                () -> new RuntimeException("Не удалось сохранить сообщение partition: " + mesMessage.getPartition()
                        + "; offset: " + mesMessage.getOffset())
        );

        if (mesMessage.getRequest() == null
                || mesMessage.getRequest().getValue() == null
                || mesMessage.getRequest().getValue().getData() == null) {
            log.warn("В поступившем запросе на аттестацию нет данных. Отправка невозможна.");
            return Optional.empty();
        }

        return Optional.of(postMesMessage(savedRequest));
    }

    private ProductAttestationResultDto postMesMessage(MesMessage mesMessage) {
        return pamSender.postAttestationRequest(mesMessage.getRequest());
    }

}
