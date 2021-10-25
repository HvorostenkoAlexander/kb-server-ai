package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PdmMessageRepository extends JpaRepository<PdmMessage, Long> {

    boolean existsByTopicAndOffsetAndPartition(String topic, long offset, int partition);

    List<PdmMessage> findByTopicAndOffsetAndPartition(String topic, long offset, int partition);

    @Query(nativeQuery = true,
            value = "SELECT * FROM pdm_message" +
                    " WHERE ?1 = topic" +
                    "   AND (is_posted IS NULL OR ?2 IS NULL " +
                    " OR CAST(CAST(?2 AS text) AS BOOLEAN) = is_posted )" +
                    " AND (ts_timestamp IS NULL OR ?3 IS NULL OR ?4 IS NULL " +
                    " OR ((date(ts_timestamp)) >= date(CAST(?3 AS timestamp with time zone))" +
                    " AND (date(ts_timestamp)) <= date(CAST(?4 AS timestamp with time zone))))"
    )
    Page<PdmMessage> getMessages(String topic,
                                 Boolean isPosted,
                                 String startDate,
                                 String endDate,
                                 PageRequest of);
}
