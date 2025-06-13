package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.entity.Reimport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimportRepository extends JpaRepository<Reimport, ReimportType> {

}
