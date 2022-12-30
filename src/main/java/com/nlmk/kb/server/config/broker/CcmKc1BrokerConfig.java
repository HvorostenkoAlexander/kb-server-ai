package com.nlmk.kb.server.config.broker;

import nlmk.l3.ccm.pgp.AttestationRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class CcmKc1BrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> ccmKc1KafkaListenerContainerFactory(CcmKc1ConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }

}
