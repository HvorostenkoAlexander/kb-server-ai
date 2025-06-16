package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.api.ReimportState;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.entity.Reimport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReimportRepository extends JpaRepository<Reimport, ReimportType> {
    @Query("SELECT r.state FROM Reimport r WHERE r.name = :type")
    ReimportState findStateById(@Param("type") ReimportType type);
}
