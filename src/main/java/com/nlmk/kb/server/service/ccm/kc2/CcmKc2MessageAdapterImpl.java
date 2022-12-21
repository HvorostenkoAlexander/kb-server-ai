package com.nlmk.kb.server.service.ccm.kc2;

import com.nlmk.attestation.product.api.pam.DataField;
import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.util.AdapterUtils;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.RequiredArgsConstructor;
import nlmk.l3.sus.kc2.AttestationRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class CcmKc2MessageAdapterImpl implements CcmMessageAdapter<AttestationRequest> {

    private final KafkaRequestAdapter<AttestationRequest> adapter;

    @Override
    public CcmMessage adapt(AttestationRequest requestMessage,
                                   String topic,
                                   String key,
                                   int partition,
                                   int offset) {

        final var attestationRequest = adapter.adapt(requestMessage);
        final var ts = new Date();

        final var ccmMessageBuilder = CcmMessage.builder()
                .partition(partition)
                .offset(offset)
                .key(key)
                .topic(topic)
                .kbSendingTs(ts)
                .kbReceiptTs(ts)
                .request(attestationRequest);

        if (attestationRequest.getValue().getData() != null) {
            ccmMessageBuilder.primeId(
                    SenderUtils.getPrimeId(attestationRequest)
            );
        }
        return ccmMessageBuilder.build();
    }

}
