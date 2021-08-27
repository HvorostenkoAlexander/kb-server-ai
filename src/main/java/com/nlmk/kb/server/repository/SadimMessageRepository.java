package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SadimMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SadimMessageRepository extends JpaRepository<SadimMessage, Long> {

    public List<SadimMessage> findSadimMessagesByParam_PrimeIdOrderByTsDesc(String primeId);

    public List<SadimMessage> findSadimMessagesByParam_MeltNoAndParam_LotNoOrderByTsDesc(Integer meltNo, Integer lotNo);

    public Optional<SadimMessage> findFirstByPartitionAndOffset(Integer partition, Long offset);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sadim_message m" +
                    " INNER JOIN sadim_pre_attestation_param p on p.id = m.param_id" +
                    " WHERE " +
                    "((date(m.ts)) >= date(CAST(?1 AS timestamp without time zone))" +
                    " AND "+
                    "(date(m.ts)) <= date(CAST(?2 AS timestamp without time zone)))" +
                    " AND (" +
                    "( ?3 IS NULL OR CAST(?3 AS CHARACTER VARYING) = p.prime_id )" +
                    "OR " +
                    "( ?4 IS NULL OR  CAST(?4 AS CHARACTER VARYING) = CAST(p.melt_no AS CHARACTER VARYING))" +
                    "OR " +
                    "( ?5 IS NULL OR  CAST(?5 AS CHARACTER VARYING) = CAST(p.lot_no AS CHARACTER VARYING))" +
                    ")"
    )
    Page<SadimMessage> findByParams(String startDate,
                                    String endDate,
                                    String primeId,
                                    Integer meltNo,
                                    Integer lotNo,
                                    PageRequest of);

    @Query(nativeQuery = true,
            value = "SELECT * FROM sadim_message m" +
                    " INNER JOIN sadim_pre_attestation_param p on p.id = m.param_id" +
                    " WHERE " +
                    "(date(m.ts)) >= date(CAST(?1 AS timestamp without time zone))" +
                    " AND (" +
                    "(date(m.ts)) <= date(CAST(?2 AS timestamp without time zone))" +
                    ")"
    )
    Page<SadimMessage> findByDates(String startDate, String endDate, PageRequest of);
}
