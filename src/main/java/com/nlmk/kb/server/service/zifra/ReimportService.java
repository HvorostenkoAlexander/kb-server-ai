package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.api.ReimportDto;
import com.nlmk.kb.server.api.ReimportRequestDto;
import com.nlmk.kb.server.api.ReimportType;

public interface ReimportService {
    /**
     * Запуск реимпорта
     */
    String startReimport(ReimportType reimportType, ReimportRequestDto requestDto);

    /**
     * Остановка реимпорта
     */
    ReimportDto stopReimport(ReimportType reimportType);

    /**
     * Получение состояния реимпорта указанного потока
     */
    ReimportDto getReimportState(ReimportType reimportType);


}
