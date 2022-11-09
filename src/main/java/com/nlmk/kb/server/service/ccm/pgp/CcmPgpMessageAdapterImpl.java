package com.nlmk.kb.server.service.ccm.pgp;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmPgpMessageAdapterImpl implements CcmMessageAdapter<AttestationRequest> {

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
            ccmMessageBuilder.primeId(attestationRequest.getValue().getData().getPrimeId());
        }
        return ccmMessageBuilder.build();
    }

}
