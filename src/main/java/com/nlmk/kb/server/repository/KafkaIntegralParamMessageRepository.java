package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;
import org.springframework.data.jpa.repository.JpaRepository;

@Deprecated
public interface KafkaIntegralParamMessageRepository extends JpaRepository<KafkaIntegralParamMessage,Long> {

    public boolean existsByOffsetAndPartition(int offset, int partition);
}
