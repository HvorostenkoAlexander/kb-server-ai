package com.nlmk.kb.server.repository;

import com.nlmk.kb.server.entity.mdm.MdmMessage;
import com.nlmk.kb.server.service.KafkaMessageRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MdmMessageRepository extends JpaRepository<MdmMessage, Long>, KafkaMessageRepository<MdmMessage> {
}
