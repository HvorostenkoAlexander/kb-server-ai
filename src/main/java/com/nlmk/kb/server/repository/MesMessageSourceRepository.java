package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.MesMessageSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MesMessageSourceRepository extends JpaRepository<MesMessageSource, Long> {

    /**
     * Поиск исходного сообщения запроса по id запроса
     */
    Optional<MesMessageSource> findByRequestId(Long requestId);

    /**
     * Поиск последнего исходного сообщения для заданного primeId
     */
    Optional<MesMessageSource> findFirstByPrimeIdOrderByCreatedAtDesc(String primeId);

}
