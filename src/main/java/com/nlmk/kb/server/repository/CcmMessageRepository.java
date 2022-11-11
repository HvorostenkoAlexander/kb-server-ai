package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    /*
    INFO: Используется запрос для удаления записи с возможным старым форматом Json request поля
    что не позволяет прочитать запись CcmMessage из базы данных в принципе
    по этой же причине не использованы отображения
    constraint для topic partition offset гарантирует наличие одной записи
    */
    @Transactional
    @Modifying
    @Query("delete from CcmMessage m WHERE m.topic = :topic AND m.partition = :partition AND m.offset = :offset")
    void deleteOldByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    List<CcmMessage> findByPrimeId(String primeId);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
