package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.SadimMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SadimMessageRepository extends JpaRepository<SadimMessage, Long> {

    public List<SadimMessage> findSadimMessagesByParam_PrimeIdOrderByTsDesc(String primeId);

    public List<SadimMessage> findSadimMessagesByParam_MeltNoAndParam_LotNoOrderByTsDesc(Integer meltNo, Integer lotNo);

    public Optional<SadimMessage> findFirstByPartitionAndOffset(Integer partition, Long offset);

    //        select p.id, p.prime_id, p.melt_no, p.lot_no, p.t12_min, p.t12_max, p.tcm_min, p.tcm_max,
//                p.pbi, p.prof_fact, p.wedge_fact, p.sqc_crit_max, p.ph1_sgp, p.ph12_sgp, p.ph23_sgp,
//                p.estimate, m.ts from public.sadim_pre_attestation_param p
//        inner join public.sadim_message m on m.param_id = p.id
//        where p.melt_no = '2111357' and m.ts > '2021-08-23'

    //pr_prod_mark IS NULL OR pr_prod_mark = '' OR ?1 IS NULL

    @Query(nativeQuery = true,
            value = "SELECT * FROM sadim_message m" +
                    " INNER JOIN sadim_pre_attestation_param p on p.id = m.param_id"+
                    " WHERE " +
                    "(date(m.ts)) >= date(CAST(?3 AS timestamp with time zone))"+
                    " AND "+
                    "?1 IS NULL OR CAST(?1 AS CHARACTER VARYING) = p.prime_id " +
                    "AND " +
                    " ?2 IS NULL OR CAST(?2 AS CHARACTER VARYING) = CAST(p.melt_no AS CHARACTER VARYING)"
                    )
    List<SadimMessage> findByParams(String primeId, Integer meltNo, String startDate);
}
