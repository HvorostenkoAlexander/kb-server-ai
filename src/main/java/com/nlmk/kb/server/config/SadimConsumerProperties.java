package com.nlmk.kb.server.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SadimConsumerProperties extends ConsumerProperties {

    public SadimConsumerProperties(@Value("${kafka.sadim.bootstrap-servers}")String kafkaServer,
                                 @Value("${kafka.sadim.group-id}")String kafkaGroupId) {
        super(kafkaServer, kafkaGroupId, null);
    }
}
