package com.core.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MapUtils {

    /**
     * Transforms a map<T, R> into a different map<V>
     */
    public static <T,R,V> V map(Map<T, R> map, BiConsumer<V, Map.Entry<T, R>> fn, V v) {
        for (Map.Entry<T, R> entry : map.entrySet()) {
            fn.accept(v, entry);
        }
        return v;
    }

    /**
     * Transforms a map<T, R> into a different map<V,W>
     */
    public static <T,R,V,W> Map<V, W> map(Map<T, R> map, BiConsumer<Map<V, W>, Map.Entry<T, R>> fn) {
        Map<V,W> result = new LinkedHashMap<>();
        for (Map.Entry<T, R> entry : map.entrySet()) {
            fn.accept(result, entry);
        }
        return result;
    }
}
