package com.core.utils;

import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class BitUtilsTest {

    @Test
    public void verifySetBit() {
        assertEquals(7, BitUtils.setBitOn(3, 2));
        assertEquals(2, BitUtils.setBitOff(3, 0));
        assertEquals(3, BitUtils.setBitOff(7, 2));
        assertEquals(1, BitUtils.setBitOff(3, 1));
    }

    @Test
    public void verifyIsBitSet() {
        assertTrue(BitUtils.isBitSet(3, 0));
        assertTrue(BitUtils.isBitSet(3, 1));
        assertFalse(BitUtils.isBitSet(3, 2));
        assertFalse(BitUtils.isBitSet(3, 3));
    }

}
