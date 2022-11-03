package com.nlmk.kb.server.service;

import com.nlmk.kb.server.exception.DateTimeParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonConverterTest {

    private final CommonConverter cct = new CommonConverterImpl();

    @Test
    void testParseDouble() {
        String str = "null";
        Double d = cct.parseToDouble(str);
        assertNull(d);

        str = null;
        d = cct.parseToDouble(str);
        assertNull(d);
    }

    @Test
    void testParseInteger() {
        String str = "null";
        Integer i = cct.parseToInteger(str);
        assertNull(i);

        str = null;
        i = cct.parseToInteger(str);
        assertNull(i);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2021-07-02T22:10:42.749-03:00",
            "2021-07-01T10:14:36+03:00",
            "2021-07-01T10:14:36"
    })
    void testParseToDate(String stringDate) {
        Date date = cct.parseToDate(stringDate);
        assertNotNull(date);

        System.out.println("date : " + date);
    }

    @Test
    void testParseToDateBad() {
        String stringDate = "2021-07-01";

        DateTimeParseException ex = assertThrows(DateTimeParseException.class,
                () -> cct.parseToDate(stringDate)
        );
        assertNotNull(ex);

        System.out.println("ex: " + ex);
    }

}
