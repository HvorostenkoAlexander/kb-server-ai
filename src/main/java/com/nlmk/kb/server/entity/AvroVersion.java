package com.nlmk.kb.server.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AvroVersion {

    private String name;
    private String description;
    private String data;

}
