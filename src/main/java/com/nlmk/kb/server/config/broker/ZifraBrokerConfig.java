package com.nlmk.kb.server.config.broker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class ZifraBrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> zifraKafkaListenerContainerFactory(ZifraConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }

}
