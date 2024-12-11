package com.nlmk.kb.server.service;

import java.util.List;

public interface KafkaMessageRepository<T> {

    boolean existsByTopicAndOffsetAndPartition(String topic, long offset, int partition);
    List<T> findByTopicAndOffsetAndPartition(String topic, long offset, int partition);

}
