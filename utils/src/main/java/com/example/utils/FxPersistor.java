package com.example.utils;

import java.util.HashMap;
import java.util.Map;

public class FxPersistor {
    public enum Type {
        FX_PANE_COLLAPSED
    }

    private final Map<String, Object> map = new HashMap<>();
    private FxPersistor(){}

    private static class FxPersistorHolder {
        private static final FxPersistor INSTANCE = new FxPersistor();
    }

    public static <T> T get(Type type, String key, T dft) {
        String mk = mapKey(type, key);
        if(FxPersistorHolder.INSTANCE.map.containsKey(mk)) {
            return (T) FxPersistorHolder.INSTANCE.map.get(mk);
        }
        return dft;
    }

    public static <T> void put(Type type, String key, T value) {
        FxPersistorHolder.INSTANCE.map.put(mapKey(type, key), value);
    }

    public static <T> void clear() {
        FxPersistorHolder.INSTANCE.map.clear();
    }

    private static String mapKey(Type type, String key) {
        return type.toString()+key;
    }
}
