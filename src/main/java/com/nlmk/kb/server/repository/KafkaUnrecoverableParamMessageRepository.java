package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.KafkaUnrecoverableParamMessage;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface KafkaUnrecoverableParamMessageRepository extends JpaRepository<KafkaUnrecoverableParamMessage,Long> {

    public boolean existsByOffsetAndPartition(int offset, int partition);
}
