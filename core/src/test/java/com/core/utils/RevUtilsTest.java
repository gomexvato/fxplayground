package com.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RevUtilsTest {
    @Test
    public void verifyToString() {
        assertEquals("4.0.0.0", RevUtils.toString(new byte[]{4,0,0,0}));
        assertEquals("4.0.0.A", RevUtils.toString(new byte[]{4,0,0,'A'}));
    }

    @Test
    public void verifyToByes() {
        assertEquals(RevUtils.toString(new byte[]{4,0,0,0}), RevUtils.toString(RevUtils.toBytes("4.0.0.0")));
        assertEquals(RevUtils.toString(new byte[]{4,0,0,'B'}), RevUtils.toString(RevUtils.toBytes("4.0.0.B")));
    }
}
