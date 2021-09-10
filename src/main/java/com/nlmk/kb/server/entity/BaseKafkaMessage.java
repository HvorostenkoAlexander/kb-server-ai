package com.nlmk.kb.server.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseKafkaMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "partition", nullable = false)
    private Integer partition;

    @Column(name = "msg_offset", nullable = false)
    private Integer offset;

    @Column(name = "msg_key", nullable = false)
    private String key;

    @Column(name = "kafka_ts", nullable = false)
    private Date kafkaTs;

    @Column(name = "kb_receipt_ts", nullable = false)
    private Date kbReceiptTs;

    @Column(name = "status")
    private String status;

    @Column(name = "note")
    private String note;
}
