package com.nlmk.kb.server.config.broker;

import com.nlmk.s3.proxy.s3notification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
public class SapBrokerConfig extends BrokerConfigBase {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, s3notification> sapKafkaListenerContainerFactory(SapConsumerProperties consumerProperties) {

        return getContainerFactory(consumerProperties);

    }

}
