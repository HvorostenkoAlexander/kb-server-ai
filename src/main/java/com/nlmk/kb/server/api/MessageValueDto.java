package com.nlmk.kb.server.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageValueDto {

    @JsonProperty("key")
    private String key;

    @JsonProperty("value")
    private JsonNode value;

}
