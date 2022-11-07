package com.nlmk.kb.server.service.ccm;

import com.nlmk.kb.server.entity.CcmMessageSource;
import com.nlmk.kb.server.repository.CcmMessageSourceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CcmMessageSourceServiceImpl implements CcmMessageSourceService {
    private final CcmMessageSourceRepository ccmMessageSourceRepository;

    @Override
    public List<CcmMessageSource> findByRequestId(Long requestId) {
        return ccmMessageSourceRepository.findByRequestId(requestId);
    }

    @Override
    public void save(Long requestId, String ccmMessageSourceString) {
            ccmMessageSourceRepository.save(CcmMessageSource.builder()
                    .requestId(requestId)
                    .messageSource(ccmMessageSourceString).build());
    }
}
