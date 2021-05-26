package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmAttestationRequestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CcmMessageRepository extends JpaRepository<CcmAttestationRequestMessage,Long> {

    boolean existsByOffsetAndPartition(int offset, int partition);
    Optional<CcmAttestationRequestMessage> findCcmAttestationRequestMessageByOffsetAndPartition(int offset, int partition);
    List<CcmAttestationRequestMessage> findCcmAttestationRequestMessageByPrimeId(String primeId);
}
