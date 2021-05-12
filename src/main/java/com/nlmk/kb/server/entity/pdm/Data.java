package com.nlmk.kb.server.entity.pdm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data implements Serializable {

    private static final long serialVersionUID = 8547635103251490690L;

    private List<Spec> specifications=new ArrayList<>();

    public void addSpec(Spec spec) {
        if (spec != null)
            this.getSpecifications().add(spec);
    }
}
