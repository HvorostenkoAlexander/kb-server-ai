package com.nlmk.kb.server.service.zifra;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
enum Catalogue {

    SP_CUSTOMER("SpCustomer", "/nsi/dict/mdm/sp_customer"),
    SP_CUSTOMER_GROUP("SpCustomerGroup", "/nsi/dict/mdm/sp_customer_group"),
    SP_GROUP_AND_CUSTOMER("SpGroupAndCustomer", "/nsi/dict/mdm/sp_group_and_customer");

    private final String code;
    private final String path;

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
