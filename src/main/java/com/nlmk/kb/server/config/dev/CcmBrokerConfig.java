package com.nlmk.kb.server.config.dev;

import com.nlmk.kb.server.config.CcmConsumerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

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
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerProperties.getKafkaGroupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put("schema.registry.url", supConsumerProperties.getSchemaRegistryUrl());
//        props.put("specific.avro.reader", "true");

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> ccmConsumerFactory() {

//        KafkaAvroDeserializer keyDeserializer = new KafkaAvroDeserializer();
//        keyDeserializer.configure(consumerConfigs(), true);
//
//        KafkaAvroDeserializer valueDeserializer = new KafkaAvroDeserializer();
//        valueDeserializer.configure(consumerConfigs(), false);

//        ErrorHandlingDeserializer<Object> errorHandlingValueDeserializer
//                = new ErrorHandlingDeserializer<>(valueDeserializer);
//
//        return new DefaultKafkaConsumerFactory<>(
//                consumerConfigs(),
//                keyDeserializer,
//                errorHandlingValueDeserializer
//        );
        return new DefaultKafkaConsumerFactory<>(
                ccmConsumerConfigs(),
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryReq() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(ccmConsumerFactory());
//        factory.setErrorHandler(new SeekToCurrentErrorHandler(
//                (record, error) -> {
//                    log.error("--- ERROR: "+error.getMessage());
//                    log.error("--- ERROR RECORD: "+record.toString());
//                }, new FixedBackOff(5000L, 1))
//        );
        factory.setConcurrency(1);

        return factory;
    }
}
