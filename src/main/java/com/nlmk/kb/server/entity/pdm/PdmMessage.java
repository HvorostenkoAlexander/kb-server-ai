package com.nlmk.kb.server.entity.pdm;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Date;

/**
 * сообщение кафка PDM-справочники
 */
@Data
@Entity
@Table(name = "pdm_message",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition","msg_offset"})
})
@TypeDef(name = "json", typeClass = JsonType.class)
public class PdmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Integer partition;

    @Column(name = "msg_offset",
            nullable = false)
    private Long offset;

    @Column(name = "msg_key",
            nullable = false)
    private String key;

    @Column(nullable = false)
    private String ts;

    @Column(nullable = false)
    private String op;

    @Column(name = "is_posted",
            nullable = false)
    private boolean isPosted=false;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PdmDictionary dictionary;

    @Column(name = "kb_receipt_ts", nullable = false)
    private Date kbReceiptTs;

    @Column(name = "note")
    private String note;
}
