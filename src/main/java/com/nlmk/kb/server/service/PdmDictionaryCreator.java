package com.nlmk.kb.server.service;

import com.nlmk.kb.server.entity.pdm.PdmDictionary;
import nlmk.l3.pdm.Data;
import nlmk.l3.pdm.Pk;
import nlmk.l3.pdm.opEnum;

public interface PdmDictionaryCreator {

    PdmDictionary createPdmDictionary(CharSequence ts,
                                      opEnum op,
                                      Pk pk,
                                      Data data);
}
