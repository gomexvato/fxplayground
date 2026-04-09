package com.core.utils;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SetUtilsTest {

    @Test
    public void verifyDeglitchTimes() {
        List<Double> values = SetUtils.decimalValues(6, set -> {
            for (double m : new double[]{1e-6, 10e-6, 100e-6, 1000e-6}) {
                for (int i = 0; i <= 255; i++) {
                    set.add(i * m);
                }
            }
        });
        assertEquals(946, values.size());
        // Values in seconds
        assertEquals("0.0", String.valueOf(values.get(0)));
        assertEquals("1.0E-6", String.valueOf(values.get(1)));
        assertEquals("2.0E-6", String.valueOf(values.get(2)));
        assertEquals("3.0E-6", String.valueOf(values.get(3)));
        assertEquals("0.254", String.valueOf(values.get(944)));
        assertEquals("0.255", String.valueOf(values.get(945)));

        // Values in microseconds (UI)
        List<Integer> microValues = ListUtils.map(values, v -> (int) (v * 1e6));
        assertEquals("0", String.valueOf(microValues.get(0)));
        assertEquals("1", String.valueOf(microValues.get(1)));
        assertEquals("2", String.valueOf(microValues.get(2)));
        assertEquals("3", String.valueOf(microValues.get(3)));
        assertEquals("254000", String.valueOf(microValues.get(944)));
        assertEquals("255000", String.valueOf(microValues.get(945)));

        // Values back to seconds (factor value)
        List<Double> fValues = ListUtils.map(microValues, v -> v / 1e6);
        assertEquals("0.0", String.valueOf(fValues.get(0)));
        assertEquals("1.0E-6", String.valueOf(fValues.get(1)));
        assertEquals("2.0E-6", String.valueOf(fValues.get(2)));
        assertEquals("3.0E-6", String.valueOf(fValues.get(3)));
        assertEquals("0.254", String.valueOf(fValues.get(944)));
        assertEquals("0.255", String.valueOf(fValues.get(945)));
    }

    @Test
    public void verifyHoldTimes() {
        List<Double> values = SetUtils.decimalValues(9, set -> {
            for (double m : new double[]{2.083333333E-8, 250e-9}) {
                for (int i = 0; i <= 255; i++) {
                    set.add(i * m);
                }
            }
        });
        assertEquals(490, values.size());
        // Values in seconds
        assertEquals("0.0", String.valueOf(values.get(0)));
        assertEquals("2.1E-8", String.valueOf(values.get(1)));
        assertEquals("4.2E-8", String.valueOf(values.get(2)));
        assertEquals("6.2E-8", String.valueOf(values.get(3)));
        assertEquals("6.35E-5", String.valueOf(values.get(488)));
        assertEquals("6.375E-5", String.valueOf(values.get(489)));

        // Values in nanoseconds (UI)
        List<Integer> microValues = ListUtils.map(values, v -> (int)(v * 1e9));
        assertEquals("0", String.valueOf(microValues.get(0)));
        assertEquals("21", String.valueOf(microValues.get(1)));
        assertEquals("42", String.valueOf(microValues.get(2)));
        assertEquals("62", String.valueOf(microValues.get(3)));
        assertEquals("63500", String.valueOf(microValues.get(488)));
        assertEquals("63750", String.valueOf(microValues.get(489)));

        // Values back to seconds (factor values)
        List<Double> fValues = ListUtils.map(microValues, v -> (v / 1e9));
        assertEquals("0.0", String.valueOf(fValues.get(0)));
        assertEquals("2.1E-8", String.valueOf(fValues.get(1)));
        assertEquals("4.2E-8", String.valueOf(fValues.get(2)));
        assertEquals("6.2E-8", String.valueOf(fValues.get(3)));
        assertEquals("6.35E-5", String.valueOf(fValues.get(488)));
        assertEquals("6.375E-5", String.valueOf(fValues.get(489)));
    }
}
