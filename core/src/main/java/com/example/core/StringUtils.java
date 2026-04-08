package com.example.utils;

public final class StringUtils {

    private StringUtils() {}

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static String requireNonEmpty(String value, String message) {
        if (isNullOrEmpty(value)) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }
}
