package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.AttestationMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttestationMessageRepository extends JpaRepository<AttestationMessage, Long> {

    /**
     * Найти последнее принятое сообщение, для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return сообщение, если есть
     */
    Optional<AttestationMessage> findFirstByPrimeIdOrderByReceiptTsDesc(String primeId);

    /**
     * Найти все принятые сообщения, для указанного идентификатора единицы металла
     *
     * @param primeId значение фильтра, идентификатор единицы металла
     * @return список сообщений, если есть
     */
    List<AttestationMessage> findByPrimeIdOrderByReceiptTsDesc(String primeId);

}
