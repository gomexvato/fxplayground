package com.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

public class RangeUtils {

    /**
     * Invokes a consumer for a range of numbers.
     * The end of an incremental range is excluded. ie. Range from 0 to 3, invokes: 0, 1, 2
     * The start of an decremental range is excluded. ie. Range from 3 to 0, invokes 2, 1, 0
     * @param start start of the range
     * @param end end of the range
     * @param consumer function consuming each value in the range
     * @return array of int
     */
    public static void range(int start, int end, Consumer<Integer> consumer) {
        for(int i=0;i<difference(start, end);i++) {
            consumer.accept(end > start ? start+i : start-i-1);
        }
    }

    /**
     * Invokes a function over a range of numbers.
     * The end of an incremental range is excluded. ie. Range from 0 to 3, invokes: 0, 1, 2
     * The start of an decremental range is excluded. ie. Range from 3 to 0, invokes 2, 1, 0
     * @param start start of the range
     * @param end end of the range
     * @param fn transforming function
     * @return array of int
     */
    public static int[] map(int start, int end, Function<Integer, Integer> fn) {
        int arrLength = difference(start, end);
        int[] arr = new int[arrLength];
        for(int i=0;i<arrLength;i++) {
            arr[i] = fn.apply(end > start ? start+i : start-i-1);
        }
        return arr;
    }

    /**
     * Invokes a function over a range of numbers.
     * The end of an incremental range is excluded. ie. Range from 0 to 3, invokes: 0, 1, 2
     * The start of an decremental range is excluded. ie. Range from 3 to 0, invokes 2, 1, 0
     * @param start start of the range
     * @param end end of the range
     * @param fn transforming function
     * @param arrayFn function instantiating the resulting array
     * @return array of T
     * @param <T>
     */
    public static <T> T[] map(int start, int end, IntFunction<T> fn, IntFunction<T[]> arrayFn) {
        int arrLength = difference(start, end);
        T[] arr = arrayFn.apply(arrLength);
        for(int i=0;i<arrLength;i++) {
            arr[i] = fn.apply(end > start ? start+i : start-i-1);
        }
        return arr;
    }

    private static int difference(int start, int end) {
        return start > end ? start - end : end - start;
    }

    public static int[] toArray(int start, int end) {
        int arrLength = difference(start, end);
        int[] arr = new int[arrLength];
        for(int i=0;i<arrLength;i++) {
            arr[i] = end > start ? start+i : start-i-1;
        }
        return arr;
    }

    /**
     * Reduces range from start to end into T
     * @param start start of range
     * @param end end of range
     * @param fn transformation
     * @param initial initial T
     * @return T
     * @param <T>
     */
    public static <T> T reduce(int start, int end, BiFunction<T, Integer, T> fn, T initial) {
        T r = initial;
        int arrLength = difference(start, end);
        for(int i=0;i<arrLength;i++) {
            r = fn.apply(r, end > start ? start+i : start-i-1);
        }
        return r;
    }

    public static int[] iFilter(int start, int end, Predicate<Integer> predicate) {
        List<Integer> list = new ArrayList<>();
        int arrLength = difference(start, end);
        for(int i=0;i<arrLength;i++) {
            int v = end > start ? start + i : start - i - 1;
            if(predicate.test(v)) {
                list.add(v);
            }
        }
        return list.stream().mapToInt(i -> i).toArray();
    }

    public static Integer[] filter(int start, int end, Predicate<Integer> predicate) {
        return Arrays.stream(iFilter(start, end, predicate)).boxed().toArray(Integer[]::new);
    }
}
