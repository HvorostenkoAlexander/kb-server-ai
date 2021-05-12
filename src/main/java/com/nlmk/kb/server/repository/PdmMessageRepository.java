package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.PdmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdmMessageRepository extends JpaRepository<PdmMessage,Long> {

    public boolean existsByTopicAndOffsetAndPartition(String topic, long offset, int partition);
}
