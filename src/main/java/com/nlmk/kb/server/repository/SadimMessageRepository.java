package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SadimMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SadimMessageRepository extends JpaRepository<SadimMessage, Long> {

    public List<SadimMessage> findSadimMessagesByParam_PrimeIdOrderByTsDesc(String primeId);

    public List<SadimMessage> findSadimMessagesByParam_MeltNoAndParam_LotNoOrderByTsDesc(Integer meltNo, Integer lotNo);

    public Optional<SadimMessage> findFirstByPartitionAndOffset(Integer partition, Long offset);
}
