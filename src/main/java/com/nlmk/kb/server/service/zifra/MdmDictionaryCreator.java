package com.nlmk.kb.server.service.zifra;

import com.nlmk.kb.server.entity.mdm.MdmDictionary;
import nlmk.l3.nsi.zifra.Data;
import nlmk.l3.nsi.zifra.pk;
import nlmk.l3.nsi.zifra.EnumOp;

public interface MdmDictionaryCreator {

    MdmDictionary createMdmDictionary(CharSequence ts,
                                      EnumOp op,
                                      pk pk,
                                      Data data);

}
