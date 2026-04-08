package com.core.utils;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RangeUtilsTest {

    @Test
    public void verifyRange() {
        final int[] values = new int[2];
        RangeUtils.range(0, 2, i -> values[i] = i);
        assertArrayEquals(new int[]{0,1}, values);

        final List<Integer> values2 = new LinkedList<>();
        RangeUtils.range(2, 5, i -> values2.add(i));
        assertEquals(new LinkedList<Integer>(){{add(2);add(3);add(4);}}, values2);

        final List<Integer> values3 = new LinkedList<>();
        RangeUtils.range(5, 2, i -> values3.add(i));
        assertEquals(new LinkedList<Integer>(){{add(4);add(3);add(2);}}, values3);
    }

    @Test
    public void verifyRangeMap() {
        assertArrayEquals(new String[]{"x-0", "x-1"}, RangeUtils.map(0, 2, i -> "x-"+i, String[]::new));
        assertArrayEquals(new String[]{"x-1", "x-0"}, RangeUtils.map(2, 0, i -> "x-"+i, String[]::new));
        assertArrayEquals(new String[]{"x-5", "x-4"}, RangeUtils.map(6, 4, i -> "x-"+i, String[]::new));
    }

    @Test
    public void verifyRangeMapInt() {
        assertArrayEquals(new int[]{0, 1}, RangeUtils.map(0, 2, i -> i));
        assertArrayEquals(new int[]{2, 3, 4}, RangeUtils.map(2, 5, i -> i));
        assertArrayEquals(new int[]{4, 3, 2}, RangeUtils.map(5, 2, i -> i));
    }

    @Test
    public void verifyToArray() {
        assertArrayEquals(new int[]{0, 1, 2}, RangeUtils.toArray(0, 3));
        assertArrayEquals(new int[]{2, 1, 0}, RangeUtils.toArray(3, 0));
    }

    @Test
    public void verifhReduce() {
        int[] values = new int[]{1, 2, 3, 4, 5};
        assertEquals("0.12345", RangeUtils.reduce(0, values.length, (a, i) -> a+values[i], "0."));
    }

    @Test
    public void verifyIntArrFilter() {
        assertArrayEquals(new int[]{3, 2, 1, 0}, RangeUtils.iFilter(4, 0, i -> i < 5));
        assertArrayEquals(new int[]{0, 1, 2}, RangeUtils.iFilter(0, 4, i -> i != 3));
        assertArrayEquals(new int[]{3}, RangeUtils.iFilter(0, 4, i -> i > 2));
        assertArrayEquals(new int[]{1, 0}, RangeUtils.iFilter(4, 0, i -> i < 2));
        assertArrayEquals(new int[]{2, 1, 0}, RangeUtils.iFilter(4, 0, i -> i != 3));
    }
}
