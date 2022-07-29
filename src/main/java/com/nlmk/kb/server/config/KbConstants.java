package com.nlmk.kb.server.config;

public class KbConstants {

    private KbConstants() {
        throw new IllegalStateException("Constants class");
    }

    public static final String REQUEST_ID_HEADER = "X-Request-ID";
    public static final String REQUEST_ID_KEY = "requestID";

    public static final String DEFAULT_PREFIX = "kb-";
    // источников запроса два: kafka topic и rest запросы
    public static final String REQUEST_PREFIX = "kb-rest-";
    public static final String KAFKA_ID = "KAFKA_ID";
    public static final String KAFKA_PREFIX = "kb-topic-";

    public static final String THROW_EXC_MESSAGE_TEMPLATE = "переброс: {0}";

}
