package com.nlmk.kb.server.service.mes.pgp;

import com.nlmk.kb.server.entity.MesMessage;
import com.nlmk.kb.server.service.ccm.KafkaRequestAdapter;
import com.nlmk.kb.server.service.mes.MesMessageAdapter;
import com.nlmk.kb.server.util.SenderUtils;
import lombok.RequiredArgsConstructor;
import nlmk.l3.mes.pgp.AttestationRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MesPgpMessageAdapterImpl implements MesMessageAdapter<AttestationRequest> {

    private final KafkaRequestAdapter<AttestationRequest> adapter;

    @Override
    public MesMessage adapt(AttestationRequest requestMessage,
                            String topic,
                            String key,
                            int partition,
                            int offset) {

        final var attestationRequest = adapter.adapt(requestMessage);
        //final var ts = new Date();

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
