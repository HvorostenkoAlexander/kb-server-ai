package com.nlmk.kb.server.service.zifra;

import lombok.Getter;
import org.apache.commons.text.CaseUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@Getter
public enum Catalogue {

    SP_CUSTOMER,
    SP_CUSTOMER_GROUP,
    SP_GROUP_AND_CUSTOMER,
    SP_OM_DATA_TYPE,
    SP_ATTRIBUTES,
    SP_PLACE,
    SP_ATTRIBUTE_ATTESTATION_GROUP,
    SP_MEASURE,
    SP_DATA_TYPE,
    SP_MARK_LABEL_APP_CHEMICAL_ANALYSIS,
    SP_CERTIFICATION_STEP_TYPE,
    SP_SAMPLING_TOPOLOGY,
    SP_DIMENSION,
    SP_PLACE_TYPE;

    private final String nsiDictMdmPath = "/nsi/dict/mdm/";

    private final String code;
    private final String path;

    Catalogue() {
        this.code = CaseUtils.toCamelCase(
                this.name().toLowerCase(Locale.ROOT),
                true,
                '_'
        );
        this.path = nsiDictMdmPath + this.name().toLowerCase(Locale.ROOT);
    }

    public static Catalogue fromCode(String code) {
        if (Objects.isNull(code)) {
            return null;
        }
        return Arrays.stream(Catalogue.values())
                .filter(s -> s.getCode().equals(code))
                .findAny()
                .orElse(null);
    }

}

