package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PdmConsumerProperties extends ConsumerProperties {

    public PdmConsumerProperties(@Value("${kafka.pdm.bootstrap-servers}")String kafkaServer,
                                 @Value("${kafka.pdm.consumer.group-id}")String kafkaGroupId,
                                 @Value("${kafka.pdm.schema.registry.url}") String schemaRegistryUrl) {
        super(kafkaServer, kafkaGroupId, schemaRegistryUrl);
    }
}
