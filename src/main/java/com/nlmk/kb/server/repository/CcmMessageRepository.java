package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    boolean existsByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    long countByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    @Query("update CcmMessage m set m.key = :key,"
            + " m.kbSendingTs = :kbSendingTs,"
            + " m.kbReceiptTs = :kbReceiptTs,"
            + " m.status = :status,"
            + " m.note = :note,"
            + " m.primeId = :primeId,"
            + " m.request = :request"
            + " WHERE m.topic = :topic AND m.partition = :partition AND m.offset = :offset")
    long updateAllByTopicAndPartitionAndOffset(String topic,
                                               int partition,
                                               int offset,
                                               String key,
                                               Date kbSendingTs,
                                               Date kbReceiptTs,
                                               String status,
                                               String note,
                                               String primeId,
                                               com.nlmk.attestation.product.api.pam.AttestationRequest request);

    List<CcmMessage> findByPrimeId(String primeId);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
