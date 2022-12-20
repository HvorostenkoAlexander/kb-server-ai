package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    void deleteByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    List<CcmMessage> findByPrimeId(String primeId);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
