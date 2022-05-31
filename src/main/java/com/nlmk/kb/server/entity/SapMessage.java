package com.nlmk.kb.server.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;

/**
 * сообщение кафка от SAP
 */
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sap_message", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"topic", "partition", "msg_offset"})
})
@TypeDef(name = "json", typeClass = JsonType.class)
public class SapMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private Integer partition;

    @Column(name = "msg_offset", nullable = false)
    private Long offset;

    @Column(name = "msg_key")
    private String key;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;

    @Column(name = "processor_version", nullable = false)
    private String processorVersion;

    @Column(name = "server", nullable = false)
    private String server;

    @Column(name = "`order`")
    private String order;

    @Column(name = "ts_timestamp", nullable = false)
    private Date ts;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private SapMessageState state;
}
