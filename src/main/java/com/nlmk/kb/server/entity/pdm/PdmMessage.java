package com.nlmk.kb.server.entity.pdm;

import com.nlmk.kb.server.entity.Operation;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

/**
 * сообщение кафка PDM-справочники
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "pdm_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
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

    @Column(name = "ts_timestamp", nullable = false)
    private Date ts;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Operation op;

    @Column(name = "is_posted",
            nullable = false)
    private boolean isPosted = false;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private PdmDictionary dictionary;

    @Column(name = "kb_receipt_ts", nullable = false)
    private Date kbReceiptTs;

    @Column(name = "note")
    private String note;
}
