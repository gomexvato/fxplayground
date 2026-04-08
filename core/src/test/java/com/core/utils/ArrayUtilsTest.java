package com.core.utils;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ArrayUtilsTest {

    @Test
    public void verifyMerge1() {
        Integer[] a1 = new Integer[]{1,2,3};
        Integer[] a2 = new Integer[]{4,5};
        assertArrayEquals(new Integer[]{1,2,3,4,5}, ArrayUtils.merge(a1, a2, Integer[]::new));
    }

    @Test
    public void verifyMergeNullAtBeginning() {
        Integer[] a1 = new Integer[]{4,5};
        assertArrayEquals(new Integer[]{null,4,5}, ArrayUtils.merge(null, a1, Integer[]::new));
    }

    @Test
    public void verifyMergeNullAtEnd() {
        Integer[] a1 = new Integer[]{4,5};
        assertArrayEquals(new Integer[]{4,5, null}, ArrayUtils.merge(a1, null, Integer[]::new));
    }

    @Test
    public void verifyMerge2() {
        Double[] a1 = new Double[]{1.0,2.1,3.2};
        Double[] a2 = new Double[]{4.3,5.4};
        assertArrayEquals(new Double[]{1.0,2.1,3.2,4.3,5.4}, ArrayUtils.merge(a1, a2, Double[]::new));
    }

    @Test
    public void verifyMerge3() {
        int[] a1 = new int[]{1,2,3};
        int[] a2 = new int[]{4,5};
        assertArrayEquals(new int[]{1,2,3,4,5}, ArrayUtils.merge(a1, a2));
    }

    @Test
    public void verifyMerge4() {
        double[] a1 = new double[]{1.0,2.1,3.2};
        double[] a2 = new double[]{4.3,5.4};
        assertArrayEquals(new double[]{1.0,2.1,3.2,4.3,5.4}, ArrayUtils.merge(a1, a2), 0);
    }

    @Test
    public void verifyFindIndex() {
        int[] a1 = new int[]{1,2,3};
        assertEquals(1, ArrayUtils.findIndex(a1, 2));
        assertEquals(-1, ArrayUtils.findIndex(a1, 4));
    }

    @Test
    public void verifyMap() {
        Integer[] arr = new Integer[]{1, 2, 3};
        assertArrayEquals(new String[]{"1-x", "2-x", "3-x"}, ArrayUtils.map(arr, i -> i + "-x", String[]::new));
    }

    @Test
    public void verifyMapToList() {
        Integer[] arr = new Integer[]{1, 2, 3};
        assertArrayEquals(new String[]{"1-x", "2-x", "3-x"},
                ArrayUtils.mapToList(arr, i -> i + "-x").toArray(new String[0]));
    }

    @Test
    public void verifyForEach() {
        Integer[] arr = new Integer[]{1, 2, 3};
        List<Integer> list = new LinkedList<>();
        ArrayUtils.forEach(arr, list::add);
        assertEquals(3, list.size());
        Assert.assertEquals(1, (int) list.get(0));
        Assert.assertEquals(2, (int) list.get(1));
        Assert.assertEquals(3, (int) list.get(2));
    }

    @Test
    public void verifyForEachWithIndex() {
        Integer[] arr = new Integer[]{1, 2, 3};
        List<String> list = new LinkedList<>();
        ArrayUtils.forEachWithIndex(arr, (v, i) -> list.add(v+":"+i));
        assertEquals(3, list.size());
        Assert.assertEquals("1:0", list.get(0));
        Assert.assertEquals("2:1", list.get(1));
        Assert.assertEquals("3:2", list.get(2));
    }

    @Test
    public void verifyHasAny() {
        Integer[] values = new Integer[]{1, 2, 3};
        assertTrue(ArrayUtils.hasAny(values, v -> v == 2));
        assertFalse(ArrayUtils.hasAny(values, v -> v == 4));
    }

    @Test
    public void verifyHas() {
        Integer[] values = new Integer[]{1, 2, 3};
        assertTrue(ArrayUtils.has(values, 2));
        assertFalse(ArrayUtils.has(values, 4));
    }

    @Test
    public void verifyReduce() {
        String[] arr = new String[]{"1", "2", "3"};
        assertEquals("123", ArrayUtils.reduce(arr, (a, i) -> a+i, ""));
    }

    @Test
    public void verifyReduceToMap() {
        String[] arr = new String[]{"1", "2", "3"};
        Map<String, Integer> map = ArrayUtils.reduceToMap(arr,
                (a, i) -> a.xput(i, Integer.parseInt(i)));
        int[] r = new int[]{map.get("1"), map.get("2"), map.get("3")};
        assertArrayEquals(r, new int[]{1,2,3});
    }

    @Test
    public void verifyFind() {
        String[] arr = new String[]{"100", "200", "300"};
        assertEquals("200", ArrayUtils.find(arr, i -> i.startsWith("2")));
    }

    @Test
    public void verifyFindInt() {
        int[] arr = new int[]{100, 200, 300};
        assertTrue(200 == ArrayUtils.find(arr, i -> i == 200));
    }

    @Test
    public void verifyGet() {
        String[] arr = new String[]{"100", "200", "300"};
        assertEquals("200", ArrayUtils.get(arr, i -> i.startsWith("2")));
    }

    @Test
    public void verifyFilter() {
        String[] arr = new String[]{"One", "Two", "Three", "Four"};
        assertArrayEquals(new String[]{"Two", "Three"}, ArrayUtils.filter(arr, i -> i.startsWith("T"), String[]::new));
    }

    @Test
    public void verifyDistinct() {
        String[] arr = new String[]{"One", "Two", "One", "Two"};
        assertArrayEquals(new String[]{"One", "Two"}, ArrayUtils.distinct(String[]::new, arr));

        Integer[] arr2 = new Integer[] {1,2,3,2,1};
        assertArrayEquals(new Integer[]{1,2,3}, ArrayUtils.distinct(Integer[]::new, arr2));

        int[] arr3 = new int[] {1,2,3,2,1};
        assertArrayEquals(new int[]{1,2,3}, ArrayUtils.distinct(arr3));
    }

    @Test
    public void verifyIndexOf() {
        int[] arr1 = new int[] {1,2,3};
        assertEquals(2, ArrayUtils.indexOf(arr1, 3));

        Integer[] arr2 = new Integer[] {1,2,3};
        assertEquals(2, ArrayUtils.indexOf(arr2, 3));
    }
}
