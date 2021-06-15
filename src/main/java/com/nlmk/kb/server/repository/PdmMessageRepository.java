package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PdmMessageRepository extends JpaRepository<PdmMessage,Long> {

    public boolean existsByTopicAndOffsetAndPartition(String topic, long offset, int partition);
    public List<PdmMessage> findByTopicAndOffsetAndPartition(String topic, long offset, int partition);
}
