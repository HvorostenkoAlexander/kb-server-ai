package com.nlmk.kb.server.util;

public class AdapterUtils {

    private AdapterUtils() {
        throw new IllegalStateException("AdapterUtils is util class");
    }

    public static Double parseFloat(Float f) {
        if (f == null) {
            return null;
        }
        return Double.parseDouble(Float.toString(f));
    }

    public static String sequenceToString(CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return sequence.toString();
    }

}
