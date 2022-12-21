package com.nlmk.kb.server.config.broker;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ZifraConsumerProperties extends ConsumerProperties {

    public ZifraConsumerProperties(@Value("${kafka.zifra.bootstrap-servers}") String kafkaServer,
                                   @Value("${kafka.zifra.consumer.group-id}") String kafkaGroupId,
                                   @Value("${kafka.zifra.schema.registry.url}") String schemaRegistryUrl,
                                   @Value("${kafka.zifra.ssl-enabled}") boolean sslEnabled,
                                   @Value("${kafka.sslTruststorePassword}") String truststorePassword,
                                   @Value("${kafka.sslKeystorePassword}") String keystorePassword,
                                   @Value("${kafka.client.truststore-path}") String truststorePath,
                                   @Value("${kafka.client.keystore-path}") String keystorePath) {
        super(kafkaServer,
                kafkaGroupId,
                schemaRegistryUrl,
                sslEnabled,
                truststorePassword,
                keystorePassword,
                truststorePath,
                keystorePath
        );
    }

}
