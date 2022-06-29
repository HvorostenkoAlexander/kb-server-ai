package com.nlmk.kb.server.config;

import com.nlmk.kb.server.config.broker.ConsumerProperties;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PdmConsumerProperties extends ConsumerProperties {

    public PdmConsumerProperties(@Value("${kafka.pdm.bootstrap-servers}")
                                         String kafkaServer,
                                 @Value("${kafka.pdm.consumer.group-id}")
                                         String kafkaGroupId,
                                 @Value("${kafka.pdm.schema.registry.url}")
                                         String schemaRegistryUrl,
                                 @Value("${kafka.sslTruststorePassword}")
                                         String truststorePassword,
                                 @Value("${kafka.sslKeystorePassword}")
                                         String keystorePassword,
                                 @Value("${kafka.client.truststore-path}")
                                         String truststorePath,
                                 @Value("${kafka.client.keystore-path}")
                                         String keystorePath) {
        super(kafkaServer,
                kafkaGroupId,
                schemaRegistryUrl,
                truststorePassword,
                keystorePassword,
                truststorePath,
                keystorePath
        );
    }
}
