package com.nlmk.kb.server.mock;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockTuple implements Tuple {

    private Map<String, String> objects = new HashMap<>();

    public MockTuple(Map<String, String> objects){
        this.objects = objects;
    }

    @Override
    public <X> X get(TupleElement<X> tupleElement) {
        throw new RuntimeException("Not supported operation...");
    }

    @Override
    public <X> X get(String alias, Class<X> type) {
        throw new RuntimeException("Not supported operation...");
    }

    @Override
    public Object get(String alias) {
        return objects.get(alias);
    }

    @Override
    public <X> X get(int i, Class<X> type) {
        throw new RuntimeException("Not supported operation...");
    }

    @Override
    public Object get(int i) {
        throw new RuntimeException("Not supported operation...");
    }

    @Override
    public Object[] toArray() {
        return objects.values().stream().toArray();
    }

    @Override
    public List<TupleElement<?>> getElements() {
        throw new RuntimeException("Not supported operation...");
    }

    @Override
    public String toString() {
        return "MockTuple{" +
                "objects=" + StringUtils.join(objects) +
                '}';
    }
}
