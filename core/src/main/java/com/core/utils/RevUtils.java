package com.core.utils;

public class RevUtils {
    public static String toString(byte[] modelRevArray) {
        if (modelRevArray == null) {
            return null;
        }

        String revString = "";
        for (byte modelRevByte : modelRevArray) {
            if (modelRevByte >= 'A' && modelRevByte <= 'Z') {
                revString += (char) modelRevByte + ".";
            } else {
                revString += modelRevByte + ".";
            }
        }
        revString = revString.substring(0, revString.lastIndexOf("."));
        return revString;
    }

    public static byte[] toBytes(String modelRevString) {
        if (modelRevString == null) {
            return null;
        }

        String[] tokens = modelRevString.split("\\.");
        byte[] revArray = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].compareTo("A") >= 0 && tokens[i].compareTo("Z") <= 0) {
                revArray[i] = tokens[i].getBytes()[0];
            } else {
                revArray[i] = Byte.parseByte(tokens[i]);
            }
        }
        return revArray;
    }
}
