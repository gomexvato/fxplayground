package com.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StringUtilsTest {

    @Test
    public void verifyRange() {
        String[] phases = StringUtils.range(1, 4, i -> "Phase " + i);
        assertTrue(phases.length == 4);
        assertEquals("Phase 1", phases[0]);
        assertEquals("Phase 2", phases[1]);
        assertEquals("Phase 3", phases[2]);
        assertEquals("Phase 4", phases[3]);
    }

    @Test
    public void verifyCombineArrays() {
        String[] a1 = new String[]{"a", "b"};
        String[] a2 = new String[]{"c", "d"};
        assertEquals(new String[]{"a", "b", "c", "d"}, StringUtils.combine(a1, a2));
    }

    @Test
    public void verifyCombineValueWithArray() {
        String[] a1 = new String[]{"a", "b"};
        assertEquals(new String[]{"c", "a", "b"}, StringUtils.combine("c", a1));
    }

    @Test
    public void verifyOrdinal() {
        assertEquals("First", StringUtils.ordinal(0));
        assertEquals("Fifth", StringUtils.ordinal(4));
        assertEquals("Twentieth", StringUtils.ordinal(19));
    }

    @Test
    public void verifyConvertToCamelCase() {
        assertEquals("TextField", StringUtils.convertToCamelCase("text_field"));
        assertEquals("A12B12C12", StringUtils.convertToCamelCase("a12_b12_c12"));
        assertEquals("A12", StringUtils.convertToCamelCase("a12"));
    }

    @Test
    public void verifyTextSpacing() {
        assertEquals("TEMP1 / GPIO / WHATEVER", StringUtils.textSpacing("TEMP1/GPIO/WHATEVER", "/"));
        assertEquals("TEMP1", StringUtils.textSpacing("TEMP1", "/"));
    }
}
