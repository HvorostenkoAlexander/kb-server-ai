package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.ResultsConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultsConfigRepository extends JpaRepository<ResultsConfig, Integer> {

    /**
     * Поиск конфигурации по наименованию головного объекта AVRO схемы
     */
    List<ResultsConfig> findByAvroName(String avroName);

}
