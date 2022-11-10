package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    /*
    INFO: Используется запрос для получения id записи с возможным старым форматом Json request поля
    что не позволяет прочитать запись CcmMessage из базы данных в принципе
    по этой же причине не использованы отображения
    constraint для topic partition offset гарантирует наличие одной записи
    */
    @Query("select m.id from CcmMessage as m"
            + " WHERE m.topic = :topic AND m.partition = :partition AND m.offset = :offset")
    List<Long> findIdByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    List<CcmMessage> findByPrimeId(String primeId);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
