package com.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

public class SetUtils {

    public static class XSet<T extends Number> {
        private final Set<T> set = new TreeSet<>();
        private final Integer roundedDecimals;

        public XSet(Integer roundedDecimals) {
            this.roundedDecimals = roundedDecimals;
        }

        public void add(T value) {
            if(roundedDecimals == null) {
                set.add(value);
            } else if (value instanceof Double) {
                set.add((T) (Double) DoubleUtils.round((Double) value, roundedDecimals));
            }
        }

        public List<T> values() {
            return new ArrayList<>(set);
        }
    }

    /**
     * Provides a list of values rounded to the defined number of decimals.
     * @param roundedDecimals
     * @param consumer
     * @return
     * @param <T>
     */
    public static <T extends Number> List<T> decimalValues(
            Integer roundedDecimals, Consumer<XSet<T>> consumer
    ) {
        XSet<T> set = new XSet(roundedDecimals);
        consumer.accept(set);
        return set.values();
    }
}
