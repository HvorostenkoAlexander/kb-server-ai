package com.nlmk.kb.server.service.mes;

import com.nlmk.kb.server.entity.MesMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MesMessageService {

    Optional<MesMessage> save(MesMessage mesMessage);

    Page<MesMessage> findAll(PageRequest of);

    MesMessage update(MesMessage mesMessage);

    /**
     * Сохранить исходное сообщение
     *
     * @param requestId              requestId запроса mes_message таблицы
     * @param primeId                primeId запроса mes_message таблицы
     * @param metalUnitId            metalUnitId запроса
     * @param mesSourceMessageString строка исходное сообщение
     * @param createdAt              дата создания
     */
    void saveSourceMessage(Long requestId, String primeId, String metalUnitId, String mesSourceMessageString, LocalDateTime createdAt);

}
