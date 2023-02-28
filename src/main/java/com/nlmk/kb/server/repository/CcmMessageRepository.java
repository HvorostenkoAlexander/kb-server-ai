package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CcmMessageRepository extends JpaRepository<CcmMessage, Long> {

    /*
     * INFO: Используется запрос для удаления записи с возможным старым форматом Json request поля.
     * Нельзя обойтись без запроса, удаление через JPA выполняется с поиском-чтением,
     * при этом устаревший формат не прочитается.
     * По полям topic, partition, offset хранится одна запись в таблице.
     */
    @Modifying
    @Query("delete from CcmMessage m WHERE m.topic = :topic AND m.partition = :partition AND m.offset = :offset")
    void deleteOldByTopicAndPartitionAndOffset(String topic, int partition, int offset);

    /**
     * Найти последнее принятое сообщение для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<CcmMessage> findFirstByPrimeIdOrderByKbReceiptTsDesc(String primeId);

}
