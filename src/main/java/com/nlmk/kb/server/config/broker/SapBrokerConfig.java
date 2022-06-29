package com.nlmk.kb.server.config.broker;

import com.nlmk.s3.proxy.s3notification;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class SapBrokerConfig {

    private final SapConsumerProperties consumerProperties;

    private ConsumerFactory<Object, Object> sapConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put("schema.registry.url", consumerProperties.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");

        if (consumerProperties.isSslEnabled()) {
            log.info("SapBrokerConfig. SSL enabled.");

            props.put("security.protocol", "SSL");
            props.put("ssl.truststore.location", consumerProperties.getTruststorePath());
            props.put("ssl.truststore.password", consumerProperties.getTruststorePassword());
            props.put("ssl.keystore.password", consumerProperties.getKeystorePassword());
            props.put("ssl.keystore.location", consumerProperties.getKeystorePath());
            props.put("ssl.endpoint.identification.algorithm", "");
        } else {
            log.info("SapBrokerConfig. SSL disabled.");
        }

        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
        keyDeserializer.configure(props, true);

        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
        valueDeserializer.configure(props, false);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new ErrorHandlingDeserializer<>(keyDeserializer),
                new ErrorHandlingDeserializer<>(valueDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, s3notification> sapKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, s3notification> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(sapConsumerFactory());
        factory.setErrorHandler(((thrownException, consumerRecord) -> log.error("ERROR", thrownException)));
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

}
