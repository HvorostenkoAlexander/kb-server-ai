package com.nlmk.kb.server.config.dev;

import com.nlmk.kb.server.config.CcmConsumerProperties;
import com.nlmk.kb.server.config.dev.deserializer.AvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("dev")
public class CcmBrokerConfig {

    private final CcmConsumerProperties consumerProperties;

    @Bean
    public Map<String, Object> ccmConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, AttestationRequest> ccmConsumerFactory() {

        return new DefaultKafkaConsumerFactory<>(
                ccmConsumerConfigs(),
                new StringDeserializer(),
                new AvroDeserializer<>(AttestationRequest.class)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> kafkaListenerContainerFactoryReq() {

        ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(ccmConsumerFactory());
        factory.setErrorHandler(((thrownException, consumerRecord) -> log.error("ERROR", thrownException)));
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}
