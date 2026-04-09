package com.core.utils;

import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapUtilsTest {

    @Test
    public void verifyMap() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>(){{
          put("One", 1);
          put("Two", 2);
          put("Three", 3);
        }};
        Map<Integer, String> map2 = MapUtils.map(map, (v, e) -> v.put(e.getValue(), e.getKey()), new LinkedHashMap<>());
        assertEquals(map2.size(), 3);
        assertEquals(map2.get(1), "One");
        assertEquals(map2.get(2), "Two");
        assertEquals(map2.get(3), "Three");
    }

    @Test
    public void verifyMap2() {
        Map<String, Integer> map = new LinkedHashMap<String, Integer>(){{
            put("One", 1);
            put("Two", 2);
            put("Three", 3);
        }};
        Map<Integer, String> map2 = MapUtils.map(map, (m, e) -> m.put(e.getValue(), e.getKey()));
        assertEquals(map2.size(), 3);
        assertEquals(map2.get(1), "One");
        assertEquals(map2.get(2), "Two");
        assertEquals(map2.get(3), "Three");
    }
}
