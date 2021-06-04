package com.nlmk.kb.server.util;

import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.impl.CommonConverterImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class CommonConverterTest {

    private CommonConverter cct = new CommonConverterImpl();

    @Test
    void testParseDouble() {
        String str = "null";
        Double d = cct.parsToDouble(str);
        assertNull(d);

        str = null;
        d = cct.parsToDouble(str);
        assertNull(d);
    }

    @Test
    void testParseInteger() {
        String str = "null";
        Integer i = cct.parsToInteger(str);
        assertNull(i);

        str = null;
        i = cct.parsToInteger(str);
        assertNull(i);
    }
}
