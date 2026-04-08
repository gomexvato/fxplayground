package com.core.utils;

import javafx.util.Pair;
import java.util.function.Predicate;

public class ValidUtils {

    /**
     * Validates value to be greater or equal to min and less or equal to max.
     * @param value
     * @param min
     * @param max
     * @return true when valid else otherwise
     */
    public static boolean isBetween(double value, double min, double max) {
        if(min > max || Double.compare(min, max) == 0) throw new IllegalArgumentException("Invalid min/max");
        return value >= min && value <= max;
    }

    /**
     * Validates the given value against the given predicates
     * Returns invalid message or null
     * @param value
     * @param validations
     * @return invalid message or null
     * @param <T>
     */
    public static <T> String validate(T value, Pair<Predicate<T>, String>... validations) {
        for(Pair<Predicate<T>, String> validation: validations) {
            if(validation.getKey().test(value)) {
                return validation.getValue();
            };
        }
        return null;
    }

    public static String validatePairs(Pair<Boolean, String>... validations) {
        for(Pair<Boolean, String> validation: validations) {
            if(validation.getKey()) {
                return validation.getValue();
            };
        }
        return null;
    }
}
