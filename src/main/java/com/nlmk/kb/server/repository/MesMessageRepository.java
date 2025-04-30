package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.MesMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MesMessageRepository extends JpaRepository<MesMessage, Long> {

    /*
     * INFO: Используется запрос для удаления записи с возможным старым форматом Json request поля.
     * Нельзя обойтись без запроса, удаление через JPA выполняется с поиском-чтением,
     * при этом устаревший формат не прочитается.
     * По полям topic, partition, offset хранится одна запись в таблице.
     */
    @Modifying
    @Query("delete from MesMessage m WHERE m.topic = :topic AND m.partition = :partition AND m.offset = :offset")
    void deleteOldByTopicAndPartitionAndOffset(String topic, int partition, int offset);

}
