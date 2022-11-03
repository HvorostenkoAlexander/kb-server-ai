package com.nlmk.kb.server.entity;

import lombok.Getter;

@Getter
public class KafkaMessageKey {

    private final String key;
    private final String schemaKey;

    private KafkaMessageKey(String key, String schemaKey) {
        this.key = key;
        this.schemaKey = schemaKey;
    }

    public static KafkaMessageKey create(String key, String schemaKey) {
        return new KafkaMessageKey(key, schemaKey);
    }

}
