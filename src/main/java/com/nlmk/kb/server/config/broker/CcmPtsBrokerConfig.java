package com.nlmk.kb.server.config.broker;

import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@ConditionalOnProperty(value = "kafka.ccm.pts.enable", matchIfMissing = true)
public class CcmPtsBrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> ccmPtsKafkaListenerContainerFactory(CcmPtsConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }

}
