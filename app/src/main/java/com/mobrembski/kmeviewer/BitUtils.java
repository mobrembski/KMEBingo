package com.mobrembski.kmeviewer;

import java.math.BigDecimal;

public class BitUtils {
    // Accuracy is number of digits of precision of converting Raw data into RPM
    private static final int RPMAccuracy = 1;
    private static final int tempMap[] = new int[]{
            //region ConversionMap
            1, 1, 2, 2, 2, 3, 3, 3, 3, 4,
            4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8,
            9, 9, 9, 10, 10, 10, 11, 11, 12, 12,
            12, 13, 13, 14, 14, 14, 15, 15, 15, 16,
            16, 17, 17, 17, 18, 18, 19, 19, 19, 20,
            20, 21, 21, 21, 22, 22, 22, 23, 23, 24,
            24, 24, 25, 25, 26, 26, 26, 27, 27, 27,
            28, 28, 29, 29, 29, 30, 30, 31, 31, 31,
            32, 32, 33, 33, 33, 34, 34, 34, 35, 35,
            36, 36, 36, 37, 37, 38, 38, 38, 39, 39,
            40, 40, 40, 41, 41, 41, 42, 42, 42, 43,
            43, 43, 44, 44, 45, 45, 45, 46, 46, 46,
            47, 47, 48, 48, 48, 49, 49, 50, 50, 50,
            51, 51, 52, 54, 54, 55, 56, 57, 58, 59,
            60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
            70, 71, 73, 74, 75, 76, 77, 78, 79, 80,
            81, 82, 83, 84, 85, 86, 87, 88, 89, 91,
            92, 93, 94, 95, 96, 97, 98, 99, 100, 101,
            102, 103, 104, 105, 106, 107, 108, 110
            //endregion
    };

    public static boolean BitIsSet(int testbyte, int bitnum) {
        return (testbyte & bitnum) == bitnum;
    }

    public static int GetMaskedBytes(int testbyte, int mask) {
        return (testbyte & mask);
    }

    private static Float precision(int decimalPlace, Float d) {

        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float GetVoltage(int rawVal) {
        return precision(2, rawVal * 0.01955f);
    }

    public static int GetTemperature(int rawVal) {
        if (rawVal < 70)
            return 0;
        return tempMap[rawVal - 70];
    }

    public static int GetRPM(int rawVal) {
        // This is a real magic. Don't ask what does this numbers means.
        // I don't know, i've just get this equation from KME.
        return 50 * RPMAccuracy * (
                (15000064 / rawVal + 25 * RPMAccuracy) / (50 * RPMAccuracy));
    }
}


