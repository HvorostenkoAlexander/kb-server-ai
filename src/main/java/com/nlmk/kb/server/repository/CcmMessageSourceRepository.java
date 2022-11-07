package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.CcmMessageSource;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CcmMessageSourceRepository extends JpaRepository<CcmMessageSource, Long> {

    /**
     * Поиск исходного сообщения запроса по id
     * @param requestId id запроса - lastRequestId
     * @return список сообщений
     */
    List<CcmMessageSource> findByRequestId(Long requestId);
}
