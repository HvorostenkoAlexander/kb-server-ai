package com.nlmk.kb.server.service.sap;

import com.nlmk.attestation.product.api.kb.SapMessageDto;
import com.nlmk.attestation.zmmorder.ZMMORDERS05DOP;
import com.nlmk.attestation.zorder.ZORDERS051;
import com.nlmk.kb.server.entity.SapMessage;
import com.nlmk.kb.server.entity.SapMessageState;
import com.nlmk.kb.server.exception.DataNotFoundException;
import com.nlmk.kb.server.repository.SapMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class SapMessageServiceImpl implements SapMessageService {

    private final SapMessageRepository sapMessageRepository;
    private final S3Service s3Service;

    private final String idoczordrsBucketName;

    public SapMessageServiceImpl(SapMessageRepository sapMessageRepository, S3Service s3Service,
                                 @Value("${s3.idoczordrs.bucket-name}") String idoczordrsBucketName) {
        this.sapMessageRepository = sapMessageRepository;
        this.s3Service = s3Service;
        this.idoczordrsBucketName = idoczordrsBucketName;
    }

    @Override
    public SapMessageDto<?> getNextSapMessage(Long id) {
        log.trace("getNextSapMessage: id [{}]", id);

        id = Objects.nonNull(id) ? id : Long.MIN_VALUE;

        SapMessage nextMessage = null;

        boolean skip = true;
        while (skip) {
            nextMessage = sapMessageRepository.findFirstByIdGreaterThanAndStateOrderById(id, SapMessageState.DONE)
                    .orElseThrow(() -> new DataNotFoundException("Сообщение не найдено"));

            skip = sapMessageRepository.existsByOrderNumAndIdGreaterThanAndState(nextMessage.getOrderNum(), nextMessage.getId(), SapMessageState.DONE);
            id = nextMessage.getId();
        }

        if (idoczordrsBucketName.equals(nextMessage.getBucket())) {
            log.trace("getNextSapMessage: id [{}]. Found zorder");
            ZORDERS051 zorder = s3Service.unmarshalZorder(nextMessage.getOrder());
            return SapMessageDto.<ZORDERS051>builder()
                    .id(nextMessage.getId())
                    .ts(nextMessage.getTs())
                    .order(zorder)
                    .orderClass(ZORDERS051.class)
                    .build();
        } else {
            log.trace("getNextSapMessage: id [{}]. Found zmmorder");
            ZMMORDERS05DOP zmmorder = s3Service.unmarshalZmmorder(nextMessage.getOrder());
            return SapMessageDto.<ZMMORDERS05DOP>builder()
                    .id(nextMessage.getId())
                    .ts(nextMessage.getTs())
                    .order(zmmorder)
                    .orderClass(ZMMORDERS05DOP.class)
                    .build();
        }
    }
}
