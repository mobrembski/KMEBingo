package com.mobrembski.kmeviewer;


public class BitUtils {

    public static boolean BitIsSet(int testbyte, int bitnum) {
        return (testbyte & bitnum) == bitnum;
    }

    public static int GetMaskedBytes(int testbyte, int mask) {
        return (testbyte & mask);
    }

}

