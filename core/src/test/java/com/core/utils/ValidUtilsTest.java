package com.core.utils;

import org.junit.Test;

import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.util.Pair;

import static org.junit.Assert.*;

public class ValidUtilsTest {

    @Test
    public void verifyValidateWithValue() {
        Pair<Predicate<Double>, String>[] validations = new Pair[] {
                new Pair<Predicate<Double>, String>(v -> v > 4, "Over 4"),
                new Pair<Predicate<Double>, String>(v -> v > 3, "Over 3")};
        assertNull(ValidUtils.validate(2.0, validations));
        assertEquals("Over 3", ValidUtils.validate(4.0, validations));
        assertEquals("Over 4", ValidUtils.validate(5.0, new Pair<>(v -> v > 4, "Over 4")));
    }

    @Test
    public void verifyValidateAnything() {
        final double v = 2.0;
        Pair<Boolean, String>[] validations = new Pair[] {
                new Pair<>(v > 4, "Over 4"),
                new Pair<>(v > 3, "Over 3")};
        assertNull(ValidUtils.validatePairs(validations));
        assertEquals("Over 1", ValidUtils.validatePairs(new Pair<>(v > 1, "Over 1")));
    }

    @Test
    public void verifyIsBetween() {
        assertTrue(ValidUtils.isBetween(3, 3, 4));
        assertTrue(ValidUtils.isBetween(4, 3, 4));
        assertFalse(ValidUtils.isBetween(2, 3, 4));
        assertFalse(ValidUtils.isBetween(5, 3, 4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyIsBetweenThrows1() {
        ValidUtils.isBetween(5, 3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyIsBetweenThrows2() {
        ValidUtils.isBetween(5, 3, 3);
    }

}
