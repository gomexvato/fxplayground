package com.core.utils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringUtils {

    /**
     * Provides an array of strings for the given range/function
     *
     * @param start start of range inclusive
     * @param end end of range inclusive
     * @param fn function that uses the range index and returns the desired string
     * @return array of strings defined by the function argument
     */
    public static String[] range(int start, int end, Function<Integer, String> fn) {
        String[] result = new String[(end - start) + 1];
        for(int i=0;i<end;i++) {
            result[i] = fn.apply(i+1);
        }
        return result;
    }

    /**
     * Combines a couple of string arrays
     * @param arr1
     * @param arr2
     * @return
     */
    public static String[] combine(String[] arr1, String[] arr2) {
        String[] result = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, result, arr1.length, arr2.length);
        return result;
    }

    /**
     * Returns a single array with value as the first item in the array
     * @param value first item
     * @param arr2 array
     * @return single array
     */
    public static String[] combine(String value, String[] arr2) {
        return combine(new String[]{value}, arr2);
    }

    private static final String[] ORDINALS = {
            "First", "Second", "Third", "Fourth", "Fifth", "Sixth",
            "Seventh", "Eighth", "Ninth", "Tenth", "Eleventh", "Twelfth",
            "Thirteenth", "Fourteenth", "Fifteenth", "Sixteenth",
            "Seventeenth", "Eighteenth", "Nineteenth", "Twentieth"};

    public static String ordinal(int number) {
        return ORDINALS[number];
    }

    public static String convertToCamelCase(String text) {
        if(text == null || text.isEmpty()) return text;
        return Arrays.stream(text.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }

    /**
     * Provides extra space before and after the given substring
     */
    public static String textSpacing(String text, String substring) {
        return text.replaceAll(substring, " "+substring + " ");
    }
}
