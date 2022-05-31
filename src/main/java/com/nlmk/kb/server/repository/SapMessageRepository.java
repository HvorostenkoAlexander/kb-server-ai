package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SapMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SapMessageRepository extends JpaRepository<SapMessage, Long> {

    Optional<SapMessage> findFirstByTopicAndOffsetAndPartition(String topic, long offset, int partition);

}
