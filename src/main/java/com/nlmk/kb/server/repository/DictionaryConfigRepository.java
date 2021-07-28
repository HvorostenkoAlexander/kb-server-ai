package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.configurator.DictionaryConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DictionaryConfigRepository extends JpaRepository<DictionaryConfig,Long> {

    Optional<DictionaryConfig> findByTopic(String topic);
}
