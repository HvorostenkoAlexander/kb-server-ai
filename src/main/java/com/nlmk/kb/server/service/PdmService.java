package com.nlmk.kb.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class PdmService {

    private final PdmMessageService messageService;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactoryPdm",
            topicPartitions = {
                    @TopicPartition(topic = "${kafka.pdm.topic.asap-chemical-properties}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.equivalents}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0")),
                    @TopicPartition(topic = "${kafka.pdm.topic.microstructure}",
                            partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))

            }
    )
    public void receiveMessageReq(@Payload ConsumerRecord request) {

        log.info("--- PDM consumer record: topic: {}; partition: {}; offset: {}, key: {}",
                request.topic(),
                request.partition(),
                request.offset(),
                request.key()
        );

        messageService.save(request);
    }
}
