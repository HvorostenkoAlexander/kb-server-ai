package com.nlmk.kb.server.service.result.sending.pgp;

import com.nlmk.attestation.product.api.ProductDto;
import com.nlmk.kb.server.service.result.configuration.ApcsAvro;
import com.nlmk.kb.server.service.result.sending.MessageProducer;
import com.nlmk.kb.server.service.result.sending.ResultSenderPgp;
import com.nlmk.kb.server.service.result.sending.ResultAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.apcs.VerificationResults;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerPgpImpl implements ApcsAvro, MessageProducer<VerificationResults> {

    private static final Schema SCHEMA = VerificationResults.SCHEMA$;
    private final ResultAdapter<VerificationResults> adapter;
    private final ResultSenderPgp sender;

    @Override
    public String getSchemaName() {
        return SCHEMA.getName();
    }

    @Override
    public String getSchemaDoc() {
        return SCHEMA.getDoc();
    }

    @Override
    public String getSchemaData() {
        return SCHEMA.toString(false);
    }

    @Override
    public void produce(ProductDto product, boolean isNew, String topic) {
        log.info("send product: id [{}], referenceId [{}] by AVRO name [{}] to topic [{}]",
                product.getId(), product.getReferenceId(), getSchemaName(), topic);

        VerificationResults results = adapter.adapt(product, isNew);
        sender.send(results, topic);
    }

    @Override
    public String getAvroName() {
        return getSchemaName();
    }

    @Override
    public Class<VerificationResults> getSendingType() {
        return VerificationResults.class;
    }

}
