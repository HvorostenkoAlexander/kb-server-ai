package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessageSource;
import com.nlmk.kb.server.entity.MesMessageSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MesMessageSourceRepository extends JpaRepository<MesMessageSource, Long> {

    /**
     * Поиск исходного сообщения запроса по id запроса
     */
    Optional<CcmMessageSource> findByRequestId(Long requestId);

    /**
     * Поиск последнего исходного сообщения для заданного primeId
     */
    Optional<CcmMessageSource> findFirstByPrimeIdOrderByCreatedAtDesc(String primeId);

}
