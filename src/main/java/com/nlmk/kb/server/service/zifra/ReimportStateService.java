package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.api.ReimportState;
import com.nlmk.kb.server.api.ReimportType;
import com.nlmk.kb.server.repository.ReimportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReimportStateService {

    private final ReimportRepository reimportRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public ReimportState getLatestState(ReimportType type) {
        return reimportRepository.findStateById(type);
    }
}