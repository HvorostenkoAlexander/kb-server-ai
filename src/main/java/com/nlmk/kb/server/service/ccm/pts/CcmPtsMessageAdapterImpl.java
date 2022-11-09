package com.nlmk.kb.server.service.ccm.pts;

import com.nlmk.kb.server.entity.CcmMessage;
import com.nlmk.kb.server.service.ccm.CcmMessageAdapter;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import nlmk.nlmk.l3.ccm.pts.DbAttestationRequestVer1;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CcmPtsMessageAdapterImpl implements CcmMessageAdapter<DbAttestationRequestVer1> {

    private final KafkaRequestAdapter<DbAttestationRequestVer1> adapter;

    @Override
    public CcmMessage adapt(DbAttestationRequestVer1 requestMessage,
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
