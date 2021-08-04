package com.nlmk.kb.server.config.prod;

import com.nlmk.kb.server.config.CcmConsumerProperties;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nlmk.l3.ccm.pgp.AttestationRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("prod")
public class CcmBrokerConfig {

    private final CcmConsumerProperties consumerProperties;

    @Bean
    public Map<String, Object> ccmConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", consumerProperties.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");

        if (consumerProperties.isProductionTopic()) {
            log.warn("Внимание! Подключаются настройки для продуктового топика ССМ");

            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.location", consumerProperties.getSslTruststorePath());
            props.put("ssl.truststore.password", consumerProperties.getSslTruststorePassword());
            props.put("ssl.keystore.password", consumerProperties.getSslKeystorePassword());
            props.put("ssl.keystore.location", consumerProperties.getSslKeystorePath());
            props.put("ssl.endpoint.identification.algorithm", "");
        } else {
            log.warn("Внимание! Подключаются настройки для тестового топика ССМ");
        }

        return props;
    }

    @Bean
    public ConsumerFactory<Object, Object> ccmConsumerFactory() {

        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
        keyDeserializer.configure(ccmConsumerConfigs(), true);

        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
        valueDeserializer.configure(ccmConsumerConfigs(), false);

        ErrorHandlingDeserializer<Object> errorHandlingKeyDeserializer
                = new ErrorHandlingDeserializer<>(keyDeserializer);

        ErrorHandlingDeserializer<Object> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                ccmConsumerConfigs(),
                errorHandlingKeyDeserializer,
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> kafkaListenerContainerFactoryReq() {

        ConcurrentKafkaListenerContainerFactory<String, AttestationRequest> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(ccmConsumerFactory());
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("--- ERROR: " + thrownException.getMessage());
            if (data!=null) {
                log.error("--- ERROR RECORD: " + data.toString());
            }
        }));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
       // factory.setConcurrency(1);
        return factory;
    }
}
