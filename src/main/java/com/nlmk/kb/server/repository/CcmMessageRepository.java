package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    boolean existsByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    List<CcmMessage> findByTopicAndPartitionAndOffset(String topic,
                                                      int partition,
                                                      int offset);

    List<CcmMessage> findByPrimeId(String primeId);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
