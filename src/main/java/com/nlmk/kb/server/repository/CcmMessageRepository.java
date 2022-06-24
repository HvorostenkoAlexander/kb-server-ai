package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CcmMessageRepository extends JpaRepository<CcmAttestationRequestMessage, Long> {

    boolean existsByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    List<CcmAttestationRequestMessage> findByTopicAndPartitionAndOffset(String topic,
                                                                        int partition,
                                                                        int offset);

    List<CcmAttestationRequestMessage> findByPrimeId(String primeId);

}
