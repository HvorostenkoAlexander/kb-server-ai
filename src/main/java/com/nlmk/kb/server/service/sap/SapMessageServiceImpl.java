package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.kb.SapMessageDto;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SapMessageServiceImpl implements SapMessageService {

    private final SapMessageRepository sapMessageRepository;
    private final S3Service s3Service;

    @Override
    public SapMessageDto getNextSapMessage(Long id) {

        id = Objects.nonNull(id) ? id : -1L;

        SapMessage nextMessage = null;

        boolean skip = true;

        while (skip) {

            nextMessage = sapMessageRepository.findFirstByIdGreaterThanAndStateOrderById(id, SapMessageState.DONE)
                    .orElseThrow(() -> new DataNotFoundException("Сообщение не найдено"));

            skip = sapMessageRepository.existsByOrderNumAndIdGreaterThanAndState(nextMessage.getOrderNum(), nextMessage.getId(), SapMessageState.DONE);
            id++;

        }

        ZORDERS051 zorder = s3Service.getZorder(nextMessage.getOrder());

        return SapMessageDto.builder()
                .id(nextMessage.getId())
                .ts(nextMessage.getTs())
                .zorder(zorder)
                .build();
    }
}
