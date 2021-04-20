package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.KafkaIntegralParamMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KafkaIntegralParamMessageRepository extends JpaRepository<KafkaIntegralParamMessage,Long> {

    public boolean existsByKey(String key);
}
