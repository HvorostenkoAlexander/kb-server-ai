package com.nlmk.kb.server.entity;

import com.nlmk.kb.server.entity.pam.AttestationRequest;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(name = "ccm_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
})
@ToString(callSuper = true)
@TypeDef(name = "json", typeClass = JsonType.class)
public class CcmAttestationRequestMessage extends BaseKafkaMessage {

    @Column(name = "prime_id")
    private String primeId;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private AttestationRequest request;
}
