package com.core.utils;


import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayUtils {

    public static int[] merge(int[]... all)  {
        return Stream.of(all).flatMapToInt(Arrays::stream).toArray();
    }

    public static double[] merge(double[] arr1, double[] arr2)  {
        double[] merged = new double[arr1.length+arr2.length];
        System.arraycopy(arr1, 0, merged, 0, arr1.length);
        System.arraycopy(arr2, 0, merged, arr1.length, arr2.length);
        return merged;
    }

    public static <T> T[] merge(T[] arr1, T[] arr2, IntFunction<T[]> generator)  {
        int arr1Length = arr1 == null ? 1 : arr1.length;
        int arr2Length = arr2 == null ? 1 : arr2.length;

        T[] merged = generator.apply(arr1Length + arr2Length);
        if(arr1 == null) {
            merged[0] = null;
            System.arraycopy(arr2, 0, merged, 1, arr2Length);
        } else {
            System.arraycopy(arr1, 0, merged, 0, arr1Length);
        }
        if(arr2 == null) {
            System.arraycopy(arr1, 0, merged, 0, arr1Length);
            merged[merged.length-1] = null;
        } else {
            System.arraycopy(arr2, 0, merged, arr1Length, arr2Length);
        }
        return merged;
    }

    public static int findIndex(int[] arr, int value) {
        for(int i=0;i<arr.length;i++) {
            if(arr[i] == value) return i;
        }
        return -1;
    }

    public static <T,R> R[] map(T[] values, Function<T, R> fn, IntFunction<R[]> generator) {
        R[] arr = generator.apply(values.length);
        for(int i=0;i<values.length;i++) {
            arr[i] = fn.apply(values[i]);
        }
        return arr;
    }

    public static <T> String[] sMap(T[] values, Function<T, String> fn) {
        return map(values, fn, String[]::new);
    }

    public static <T> Integer[] iMap(T[] values, Function<T, Integer> fn) {
        return map(values, fn, Integer[]::new);
    }

    public static <T,R> List<R> mapToList(T[] values, Function<T, R> fn) {
        return Arrays.stream(values).map(fn).collect(Collectors.toList());
    }

    public static <T> void forEach(T[] values, Consumer<T> consumer) {
        for(T value: values) {
            consumer.accept(value);
        }
    }

    public static <T> void forEachWithIndex(T[] arr, BiConsumer<T, Integer> consumer) {
        for(int i=0;i<arr.length;i++) {
            consumer.accept(arr[i], i);
        }
    }

    public static void forEachWithIndex(int[] arr, BiConsumer<Integer, Integer> consumer) {
        for(int i=0;i<arr.length;i++) {
            consumer.accept(arr[i], i);
        }
    }

    public static <T> boolean hasAny(T[] values, Predicate<T> predicate) {
        for(T value: values) {
            if(predicate.test(value)) return true;
        }
        return false;
    }

    public static <T> boolean has(T[] values, T t) {
        return hasAny(values, v -> v.equals(t));
    }

    /**
     * Reduces T[] to R
     */
    public static <T,R> R reduce(T[] values, BiFunction<R, T, R> fn, R initialR) {
        R r = initialR;
        for (T value : values) {
            r = fn.apply(r, value);
        }
        return r;
    }

    public static class XMap<K,V> extends HashMap<K,V> {
        public XMap<K, V> xput(K k, V v) {
            super.put(k, v);
            return this;
        }
    }

    /**
     * Reduces T[] to Map<K,V>
     */
    public static <T,K,V> Map<K,V> reduceToMap(
            T[] values, BiFunction<XMap<K,V>, T, XMap<K,V>> fn) {
        XMap<K,V> r = new XMap<>();
        for (T value : values) {
            r = fn.apply(r, value);
        }
        return r;
    }

    /**
     * Returns the first value that meets the predicate,
     * or throws an exception if none of the values meet the predicate
     * @param values
     * @param predicate
     * @return
     * @param <T>
     */
    public static <T> T get(T[] values, Predicate<T> predicate) {
        for(T value: values) {
            if(predicate.test(value)) return value;
        }
        throw new RuntimeException("Unable to find expected value!!");
    }

    /**
     * Returns the first value that meets the predicate,
     * otherwise returns null
     * @param values
     * @param predicate
     * @return
     * @param <T>
     */
    public static <T> T find(T[] values, Predicate<T> predicate) {
        for(T value: values) {
            if(predicate.test(value)) return value;
        }
        return null;
    }

    /**
     * Returns the first value that meets the predicate,
     * otherwise returns null
     * @param values
     * @param predicate
     * @return
     */
    public static Integer find(int[] values, Predicate<Integer> predicate) {
        for(int value: values) {
            if(predicate.test(value)) return value;
        }
        return null;
    }

    public static <T> T[] filter(T[] values, Predicate<T> predicate, IntFunction<T[]> generator) {
        List<T> r = new LinkedList<T>();
        for(T value: values) {
            if(predicate.test(value)) r.add(value);
        }
        return r.toArray(generator.apply(r.size()));
    }

    @SafeVarargs
    public static <T> T[] distinct(IntFunction<T[]> generator, T[]... all) {
        return Stream.of(all).flatMap(Arrays::stream).distinct().toArray(generator);
    }

    public static int[] distinct(int[]... all) {
        return Stream.of(all).flatMapToInt(Arrays::stream).distinct().toArray();
    }

    public static int indexOf(int[] arr, int target) {
        for(int i=0;i<arr.length;i++) {
            if(arr[i]==target) {
                return i;
            }
        }
        return -1;
    }

    public static <T> int indexOf(T[] arr, T t) {
        for(int i=0;i<arr.length;i++) {
            if(t == null ? arr[i] == null : t.equals(arr[i])) {
                return i;
            }
        }
        return -1;
    }
}
