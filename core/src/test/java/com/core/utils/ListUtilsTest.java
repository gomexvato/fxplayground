package com.core.utils;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static com.core.utils.ListUtils.toMap;
import static com.core.utils.ListUtils.toList;
import static org.junit.Assert.*;

public class ListUtilsTest {

    @Test
    public void verifyToList() {
        List<String> strings = Arrays.asList("one", "two", "three");
        List<String> expected = Arrays.asList("onex", "twox", "threex");
        assertEquals(expected, toList(strings, s -> s + "x"));
    }

    @Test
    public void verifyToMap() {
        List<String> strings = Arrays.asList("one", "two", "three");
        Map<String, String> expected = new HashMap<String, String>();
        expected.put("one", "onex");
        expected.put("two", "twox");
        expected.put("three", "threex");
        assertEquals(expected, toMap(strings, s -> s + "x"));
    }

    @Test
    public void verifyFilter() {
        List<String> list1 = Arrays.asList("one", "two", "three");
        List<String> filter = Arrays.asList("two");
        assertEquals(ListUtils.filter(list1, filter), Arrays.asList("one", "three"));
    }

    @Test
    public void verifyFilter2() {
        List<String> list1 = Arrays.asList("one", "two", "three");
        assertEquals(ListUtils.filter(list1, (x) -> x.startsWith("t")), Arrays.asList("two", "three"));
    }

    @Test
    public void verifyAnyMatch() {
        List<String> list = Arrays.asList("one", "two", "three");
        assertTrue(ListUtils.anyMatch(list, (String x) -> x.equals("one")));
    }

    @Test
    public void verifyAnyMatchWithException() {
        List<String> list = Arrays.asList("one", "two", "three");
        assertFalse(ListUtils.anyMatch(list, (String x) -> {
            throw new RuntimeException("ie");
        }));
    }

    @Test
    public void verifyAllCompletableFutures() throws ExecutionException, InterruptedException {
        CompletableFuture f1 = new CompletableFuture();
        CompletableFuture f2 = new CompletableFuture();

        Executors.newCachedThreadPool().submit(() -> {
            f1.complete("one");
            f2.complete("two");
            return null;
        });

        List<CompletableFuture<String>> futures = Arrays.asList(f1, f2);
        assertEquals(ListUtils.all(futures).get(),
                Arrays.asList("one", "two"));
    }

    @Test
    public void verifySum() {
        List<String> list = Arrays.asList("one", "two", "three");
        assertEquals(11, ListUtils.sum(list, (String word) -> word.length()));
    }

    @Test
    public void verifySort() {
        List<String> list = Arrays.asList("one", "two", "three");
        List<String> expected = Arrays.asList("one", "three", "two");
        assertEquals(expected, ListUtils.sort(list));
    }

    @Test
    public void verifyIntRange() {
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), ListUtils.intRange(1, 5));
    }

    @Test
    public void verifyChunks1() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        List<List<Integer>> batches = ListUtils.chunks(values, 2);
        assertEquals(2, batches.size());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), batches.get(0));
        assertEquals(Arrays.asList(7, 8, 9, 10, 11), batches.get(1));
    }

    @Test
    public void verifyChunks2() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        List<List<Integer>> batches = ListUtils.chunks(values, 3);
        assertEquals(3, batches.size());
        assertEquals(ListUtils.intRange(1, 4), batches.get(0));
        assertEquals(ListUtils.intRange(5, 8), batches.get(1));
        assertEquals(ListUtils.intRange(9, 11), batches.get(2));
    }

    @Test
    public void verifyChunks3() {
        List<Integer> values = ListUtils.intRange(1, 73);
        List<List<Integer>> batches = ListUtils.chunks(values, 12);
        assertEquals(11, batches.size());
        assertEquals(ListUtils.intRange(1, 7), batches.get(0));
        assertEquals(ListUtils.intRange(8, 14), batches.get(1));
        assertEquals(ListUtils.intRange(64, 70), batches.get(9));
        assertEquals(ListUtils.intRange(71, 73), batches.get(10));
    }

    @Test
    public void verifyMap() {
        List<Integer> list = new ArrayList(){{add(1);add(2);add(3);}};
        List<String> result = new ArrayList(){{add("x-1");add("x-2");add("x-3");}};
        assertEquals(result, ListUtils.map(list, item -> "x-"+item));
    }

    @Test
    public void verifyFindClosest() {
        List<Double> values = Arrays.asList(1.0, 2.0, 5.0, 8.0);
        assertEquals(2.0, ListUtils.findClosest(values, 2.0), 0);
        assertEquals(2.0, ListUtils.findClosest(values, 3.0), 0);
        assertEquals(5.0, ListUtils.findClosest(values, 4.0), 0);
        assertEquals(5.0, ListUtils.findClosest(values, 6.0), 0);
        assertEquals(8.0, ListUtils.findClosest(values, 7.0), 0);
        assertEquals(8.0, ListUtils.findClosest(values, 8.0), 0);
    }

    class Pin {
        final int number;
        Pin(int number) {
            this.number = number;
        }
    }

    @Test
    public void verifyDistinctByKey() {
        List<Pin> list = Arrays.asList(new Pin(1), new Pin(2), new Pin(2));
        List<Pin> list2 = ListUtils.distinctByKey(list, p -> p.number);
        assertEquals(2, list2.size());
        assertEquals(1, list2.get(0).number);
        assertEquals(2, list2.get(1).number);
    }
}