package com.nlmk.kb.server.util;

import com.nlmk.kb.server.service.CommonConverter;
import com.nlmk.kb.server.service.impl.CommonConverterImpl;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Test
    void testDate() throws ParseException {
        String stringDate = "2021-07-01T10:14:36+03:00";

       // Date date = cct.parseToDate(stringDate);
      //  Дата и время передачи в формате UTC YYYY-MM-DD"T"HH24:MI:SS.FF3±hh:mm
        //"yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

        val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        val date = format.parse(stringDate);
        System.out.println("--- date: "+date);
    }
}
