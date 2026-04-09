package com.core.utils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ListUtils {
    public static<T> List<T> toList(Set<Object> set) {
        return new LinkedList(set);
    }

    public static<T, R> List<R> toList(List<T> list, Function<T, R> fn) {
        return list.stream().map(fn::apply).collect(Collectors.toList());
    }

    public static<T, R> Map<T, R> toMap(List<T> list, Function<T, R> fn) {
        return list.stream().collect(Collectors.toMap(Function.identity(), fn::apply));
    }

    public static<T> List<T> filter(List<T> values, List<T> filter) {
        return values.stream().filter(x -> !filter.contains(x)).collect(Collectors.toList());
    }

    public static<T> List<T> filter(List<T> values, Function<T, Boolean> filter) {
        return values.stream().filter(filter::apply).collect(Collectors.toList());
    }

    public static<T> CompletableFuture<List<T>> all(List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[futures.size()]);
        return CompletableFuture.allOf(cfs)
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    public static<T> boolean anyMatch(List<T> list, Function<T, Boolean> predicate) {
        return list.stream().anyMatch(t -> {
            try {
                return predicate.apply(t);
            } catch(Exception e) {
                return false;
            }
        });
    }

    public static<T> int sum(List<T> list, Function<T, Integer> fn) {
        return list.stream().reduce(0, (total, x) -> total + fn.apply(x), Integer::sum);
    }

    public static<T> List<T> sort(List<T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Subdivide a list into a number of sublists
     * @param list list to sub-divide
     * @param numSublists how many sublists to subdivide into
     * @return
     * @param <T>
     */
    public static <T> List<List<T>> chunks(List<T> list, int numSublists) {
        if (numSublists <= 0) throw new IllegalArgumentException("invalid numSublists!");
        int size = list.size();
        if (size <= 0) return Collections.emptyList();
        if (size < numSublists) return new ArrayList<List<T>>() {{
            add(list);
        }};
        int itemsPerChunk = (int) Math.ceil((double) size / numSublists);
        int numIterations = size / itemsPerChunk;
        List<List<T>> r = new ArrayList<>();
        for (int i = 0; i < numIterations; i++) {
            int start = i * itemsPerChunk;
            r.add(list.subList(start, start + itemsPerChunk));
        }
        r.add(list.subList(numIterations * itemsPerChunk, list.size()));
        return r;
    }

    /**
     * Returns a range of integers
     * @param start starting point of range inclusive. ie: 1
     * @param end ending point of range inclusive. ie: 5
     * @return [1, 2, 3, 4, 5]
     */
    public static List<Integer> intRange(int start, int end) {
        return IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
    }

    /**
     * Transforms a list of T into a list of R
     * @param list of T
     * @param fn to transform T into R
     * @return list of R
     */
    public static <T,R> List<R> map(List<T> list, Function<T, R> fn) {
        return list.stream().map(fn).collect(Collectors.toList());
    }

    /**
     * Finds closest value in list of doubles
     * @param target
     * @param list
     * @return
     */
    public static double findClosest(List<Double> list, double target) {
        double closest = list.get(0);
        double minDifference = Math.abs(target - closest);
        for(double d: list) {
            double currentDiff = Math.abs(target - d);
            if(currentDiff < minDifference) {
                minDifference = currentDiff;
                closest = d;
            } else if(currentDiff == minDifference) {
                closest = Math.max(closest, d);
            }
        }
        return closest;
    }

    public static <T> Predicate<T> distinctByKeyPredicate(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T,V> List<T> distinctByKey(List<T> list, Function<T, V> keyFn) {
        return list.stream()
                .filter(distinctByKeyPredicate(keyFn))
                .collect(Collectors.toList());
    }
}