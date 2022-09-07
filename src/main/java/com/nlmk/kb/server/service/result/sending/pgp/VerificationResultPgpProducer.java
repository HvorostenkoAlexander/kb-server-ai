package com.nlmk.kb.server.service.result.sending.pgp;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.sending.VerificationResultSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationResultPgpProducer implements ApcsAvro, MessageProducer {

    private static final Schema SCHEMA = VerificationResults.SCHEMA$;
    private final VerificationResultsPgpAdapter adapter;
    private final VerificationResultSender sender;

    @Override
    public String getName() {
        return SCHEMA.getName();
    }

    @Override
    public String getDescription() {
        return SCHEMA.getDoc();
    }

    @Override
    public String getData() {
        return SCHEMA.toString(false);
    }

    @Override
    public void produce(ProductDto product, boolean isNew, String topic) {
        log.info("send product: id [{}], referenceId [{}] by AVRO [{}] to topic [{}]",
                product.getId(), product.getReferenceId(), getDescription(), topic);

        VerificationResults results = adapter.adapt(product, isNew);
        sender.send(results, topic);
    }

    @Override
    public String getType() {
        return getDescription();
    }

}
