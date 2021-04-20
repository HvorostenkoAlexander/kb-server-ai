package com.nlmk.kb.server.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@Builder
@Entity
@Table(name = "messages_of_integral_parameters")
public class KafkaIntegralParamMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    private Long id;

    @Column(unique = true, nullable = false, name="message_key") // todo уточнить об уникальности ключа в разных партициях
    private String key;

    @Column(nullable = false)
    private String timestamp;

    @Column(nullable = false,name="message_offset")
    private int offset;

    @Column(nullable = false)
    private String topic;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(unique = false)
    private IntegralParam param;
}
