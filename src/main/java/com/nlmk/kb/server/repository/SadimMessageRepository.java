package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SadimMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SadimMessageRepository extends JpaRepository<SadimMessage,Long> {

  public List<SadimMessage> findSadimMessagesByParam_PrimeIdOrderByTs(String primeId);
}
