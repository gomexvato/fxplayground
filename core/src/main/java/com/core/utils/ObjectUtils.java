package com.core.utils;

public class ObjectUtils {
    public static <T> boolean equals(T value1, T value2) {
        if(value1 instanceof Double) {
            return DoubleUtils.equals((Double) value1, (Double) value2);
        }
        if(value1 instanceof Integer) {
            return ((Integer)value1).intValue() == ((Integer)value2).intValue();
        }
        return value1.equals(value2);
    }
}
