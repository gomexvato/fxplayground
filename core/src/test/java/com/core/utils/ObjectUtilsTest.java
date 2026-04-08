package com.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectUtilsTest {
    @Test
    public void verifyEquals() {
        assertTrue(ObjectUtils.equals(1, 1));
        assertFalse(ObjectUtils.equals(1, 2));

        assertTrue(ObjectUtils.equals(1.1, 1.1));
        assertFalse(ObjectUtils.equals(1.1, 1.2));

        // micros
        assertTrue(ObjectUtils.equals(.00000111, .00000111));
        assertFalse(ObjectUtils.equals(.00000111, .00000112));

        // nanos
        assertTrue(ObjectUtils.equals(.0000000011, .0000000011));
        assertFalse(ObjectUtils.equals(.0000000011, .0000000012));

        assertTrue(ObjectUtils.equals("simon", "simon"));
        assertFalse(ObjectUtils.equals("simon", "simona"));
    }
}
