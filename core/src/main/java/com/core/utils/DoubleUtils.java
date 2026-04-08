package com.core.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {

    public static double round(double value, int precision) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean equals(double val1, double val2, double epsilon) {
        return Math.abs(val1 - val2) < epsilon;
    }
    // Used to compare nanoseconds
    //
    public static boolean equals(double val1, double val2) {
        return equals(val1, val2, .0000000001);
    }

    public static double diff(double v1, double v2) {
        return v1 > v2
                ? BigDecimal.valueOf(v1).subtract(BigDecimal.valueOf(v2)).doubleValue()
                : BigDecimal.valueOf(v2).subtract(BigDecimal.valueOf(v1)).doubleValue();
    }
}
