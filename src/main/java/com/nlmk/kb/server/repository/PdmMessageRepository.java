package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.pdm.PdmMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PdmMessageRepository extends JpaRepository<PdmMessage, Long> {

    public boolean existsByTopicAndOffsetAndPartition(String topic, long offset, int partition);

    public List<PdmMessage> findByTopicAndOffsetAndPartition(String topic, long offset, int partition);

    @Query(nativeQuery = true,
            value = "SELECT * FROM pdm_message" +
                    " WHERE ?1 = topic" +
                    "   AND (is_posted IS NULL OR ?2 IS NULL " +
                    "OR CAST(CAST(?2 AS text) AS BOOLEAN) = is_posted )")
    Page<PdmMessage> getMessages(String topic, Boolean isPosted, PageRequest of);
}
