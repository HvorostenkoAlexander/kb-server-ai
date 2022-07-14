package com.nlmk.kb.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MessagesBatchDto {

    @JsonProperty("key_schema")
    private String keySchema;

    @JsonProperty("value_schema")
    private String valueSchema;

    /**
     * Для возможности передачи в Kafka-Rest одним сообщением сведений о нескольких объектах применяется List
     */
    @JsonProperty("records")
    private List<MessageValueDto> records;

}
