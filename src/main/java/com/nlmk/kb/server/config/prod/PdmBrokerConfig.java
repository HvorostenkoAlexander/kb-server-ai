package com.nlmk.kb.server.config.prod;

import com.nlmk.kb.server.config.PdmConsumerProperties;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
public class PdmBrokerConfig {

    private final PdmConsumerProperties consumerProperties;

    @Bean
    @Primary
    public ConsumerFactory<Object,Object> pdmConsumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerProperties.getKafkaServer());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("schema.registry.url", consumerProperties.getSchemaRegistryUrl());
        props.put("specific.avro.reader", "true");

        props.put("security.protocol", "SSL");
        props.put("ssl.truststore.location", "kafkaSsl/client.truststore.jks");
        props.put("ssl.truststore.password", "assagai");
        props.put("ssl.keystore.password", "assagai");
        props.put("ssl.keystore.location", "kafkaSsl/client.keystore.jks");
        props.put("ssl.endpoint.identification.algorithm", "");

        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
        keyDeserializer.configure(props, true);

        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
        valueDeserializer.configure(props, false);

        ErrorHandlingDeserializer<Object> errorHandlingValueDeserializer
                = new ErrorHandlingDeserializer<>(valueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                keyDeserializer,
                errorHandlingValueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactoryPdm() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(pdmConsumerFactory());
        factory.setErrorHandler(((thrownException, data) -> {
            log.error("--- ERROR: " +thrownException.getClass()+"; "+ thrownException.getMessage());
            if (data!=null) {
                log.error("--- ERROR RECORD: " + data.toString());
            }
        }));
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        //factory.setConcurrency(1);
        return factory;
    }
}
