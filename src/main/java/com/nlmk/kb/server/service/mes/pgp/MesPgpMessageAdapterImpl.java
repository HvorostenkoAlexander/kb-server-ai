package com.nlmk.kb.server.service.mes.pgp;

import com.nlmk.kb.server.entity.MesMessage;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.service.mes.MesMessageAdapter;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.RequiredArgsConstructor;
import nlmk.mes.cgp.asap.adapter.analysis.request.v1.AsapAnalysisRequestVer1;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MesPgpMessageAdapterImpl implements MesMessageAdapter<AsapAnalysisRequestVer1> {

    private final KafkaRequestAdapter<AsapAnalysisRequestVer1> adapter;

    @Override
    public MesMessage adapt(AsapAnalysisRequestVer1 requestMessage,
                            String topic,
                            String key,
                            int partition,
                            int offset) {

        final var attestationRequest = adapter.adapt(requestMessage);

        final var mesMessageBuilder = MesMessage.builder()
                .partition(partition)
                .offset(offset)
                .key(key)
                .topic(topic)
                .request(attestationRequest);

        if (attestationRequest.getValue().getData() != null) {
            mesMessageBuilder.metalUnitId(
                    UUID.fromString(Objects.requireNonNull(SenderUtils.getPrimeId(attestationRequest)))
            );
        }
        return mesMessageBuilder.build();
    }
}
