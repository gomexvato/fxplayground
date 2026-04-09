package com.core.utils;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class HexUtilsTest {

    @Test
    public void verifyHexToBytesAndViceversa() {
        String hex = "8FFFFFFF";
        byte[] bytes = HexUtils.hexToBytes(hex);
        assertEquals("0x"+hex, HexUtils.bytesToHex(bytes));
    }

    @Test
    public void verifyHexToBytesAndViceversaWhenHexStartsWith0x() {
        String hex = "0x8FFFFFFF";
        byte[] bytes = HexUtils.hexToBytes(hex);
        assertEquals(hex, HexUtils.bytesToHex(bytes));
    }

    @Test
    public void verifyStrip0xh (){
        assertEquals("FA", HexUtils.strip0xh("0xFAh"));
        assertEquals("F7", HexUtils.strip0xh("0XF7"));
        assertEquals("F8", HexUtils.strip0xh("F8H"));
        assertEquals("f9", HexUtils.strip0xh("f9"));
        assertEquals("F07", HexUtils.strip0xh("0xF07h"));
    }
}