package com.core.utils;

import java.util.BitSet;

public class BitUtils {

    public static int setBitOn(int number, int index) {
        return setBit(number, index, true);
    }

    public static int setBitOff(int number, int index) {
        return setBit(number, index, false);
    }

    public static int setBit(int number, int index, boolean on) {
        return on
                ? number | (1 << index)
                : number & ~(1 << index);
    }

    public static boolean isBitSet(int number, int index) {
        return BitSet.valueOf(new long[]{number}).get(index);
    }
}
