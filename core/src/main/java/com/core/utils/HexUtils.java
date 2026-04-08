package com.core.utils;

public class HexUtils {
    public static byte[] hexToBytes(String hex) {
        if(hex.startsWith("0x")) hex = hex.substring(2);
        int len = hex.length();
        byte[] data = new byte[len/2];
        for(int i=0; i<len; i+=2) {
            data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder("0x");
        for (byte b : bytes) {
            sb.append(String.format("%02x", b).toUpperCase());
        }
        return sb.toString();
    }

    public static String strip0xh (String value){
        return value.replaceAll("^(0[x|X])?(.*?)(H|h)?$", "$2");
    }

    /**
     * Converts hex value (0xFFh) to Short
     * @param value
     * @return returns Short value or Null for invalid hex
     */
    public static Short hexToShort (String value){
        try {
            return Short.parseShort(strip0xh(value), 16);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}