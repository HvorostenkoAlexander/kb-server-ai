package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import lombok.RequiredArgsConstructor;
import nlmk.l3.ccm.pts.AttestationRequest;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class CcmPtsMessageAdapterImpl implements CcmMessageAdapter<AttestationRequest> {

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
