package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.integral.IntegralParamsMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegralParamsMessageRepository extends JpaRepository<IntegralParamsMessage, Long> {

    void deleteByPrimeId(String primeId);
}
