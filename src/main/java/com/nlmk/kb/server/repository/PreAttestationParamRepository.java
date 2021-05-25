package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.PreAttestationParam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreAttestationParamRepository extends JpaRepository<PreAttestationParam,Long> {

    public List<PreAttestationParam> findByPrimeId(String primeId);
}
