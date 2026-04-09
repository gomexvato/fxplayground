package com.core.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleUtilsTest {

    @Test
    public void verifyRound() {
        assertEquals(0.04, DoubleUtils.round(0.044, 2), 0);
        assertEquals(0.05, DoubleUtils.round(0.045, 2), 0);
        assertEquals(0.05, DoubleUtils.round(0.046, 2), 0);
        assertEquals(0.04667, DoubleUtils.round(0.046666666, 5), 0);;
    }

    @Test
    public void verifyEquals() {
        assertTrue(DoubleUtils.equals(
                10.0 / (10 + 200) * 1000.0,
                10.0 / (10 + 200) * 1000.0));
        assertFalse(DoubleUtils.equals(
                10.0 / (10 + 199.999999) * 1000.0,
                10.0 / (10 + 200) * 1000.0));
    }

    @Test
    public void verifyDiff() {
        assertEquals(0.1, DoubleUtils.diff(1.1, 1.2), 0);
        assertEquals(0.1, DoubleUtils.diff(1.2, 1.1), 0);
        assertEquals(0, DoubleUtils.diff(1.1, 1.1), 0);
    }
}
