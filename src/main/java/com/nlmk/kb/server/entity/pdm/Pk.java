package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pk implements Serializable {

    private static final long serialVersionUID = -8548587551703534367L;

    private String Id;
    private String systemCode;
    private String directoryId;
}
