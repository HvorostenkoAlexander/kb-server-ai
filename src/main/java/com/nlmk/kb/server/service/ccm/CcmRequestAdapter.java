package com.nlmk.kb.server.service.ccm;

public abstract class CcmRequestAdapter {

    protected Double parseFloat(Float f) {
        if (f == null) {
            return null;
        }
        return Double.parseDouble(Float.toString(f));
    }

    protected String sequenceToString(CharSequence sequence) {
        if (sequence == null) {
            return null;
        }
        return sequence.toString();
    }

}
