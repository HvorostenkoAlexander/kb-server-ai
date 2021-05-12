package com.nlmk.kb.server.entity.pdm;

import com.nlmk.kb.server.entity.Operation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class PdmDictionary implements Serializable {

    private static final long serialVersionUID = -5120760425855505772L;

    private String ts;
    private String op;
    private Pk pk;
    private Data data;
}
