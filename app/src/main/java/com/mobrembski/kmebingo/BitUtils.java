package com.mobrembski.kmebingo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BitUtils {
    // Accuracy is number of digits of precision of converting Raw data into RPM
    private static final int RPMAccuracy = 1;
    private static final int tempMap[] = new int[]{
            //region Temperature Conversion Map
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
    //private static SparseArray<int[]> rpmMap = new SparseArray<int[]>() {{
    private static final Map<Integer,int[]> rpmMap = new TreeMap<Integer, int[]>() {{
            //region RPM Conversion Map
            put(1000, new int[] {0x3A, 0x98});
            put(1100, new int[] {0x35, 0x44});
            put(1200, new int[] {0x30, 0xD4});
            put(1300, new int[] {0x2D, 0x12});
            put(1400, new int[] {0x29, 0xDA});
            put(1500, new int[] {0x27, 0x10});
            put(1600, new int[] {0x24, 0x9F});
            put(1700, new int[] {0x22, 0x77});
            put(1800, new int[] {0x20, 0x8D});
            put(1900, new int[] {0x1E, 0xD6});
            put(2000, new int[] {0x1D, 0x4C});
            put(2100, new int[] {0x1B, 0xE6});
            put(2200, new int[] {0x1A, 0xA2});
            put(2300, new int[] {0x19, 0x79});
            put(2400, new int[] {0x18, 0x6A});
            put(2500, new int[] {0x17, 0x70});
            put(2600, new int[] {0x16, 0x89});
            put(2700, new int[] {0x15, 0xB3});
            put(2800, new int[] {0x14, 0xED});
            put(2900, new int[] {0x14, 0x34});
            put(3000, new int[] {0x13, 0x88});
            put(3100, new int[] {0x12, 0xE6});
            put(3200, new int[] {0x12, 0x4F});
            put(3300, new int[] {0x11, 0xC1});
            put(3400, new int[] {0x11, 0x3B});
            put(3500, new int[] {0x10, 0xBD});
            put(3600, new int[] {0x10, 0x46});
            put(3700, new int[] {0x0F, 0xD6});
            put(3800, new int[] {0x0F, 0x6B});
            put(3900, new int[] {0x0F, 0x05});
            put(4000, new int[] {0x0E, 0xA6});
            put(4100, new int[] {0x0E, 0x4A});
            put(4200, new int[] {0x0D, 0xF3});
            put(4300, new int[] {0x0D, 0xA0});
            put(4400, new int[] {0x0D, 0x51});
            put(4500, new int[] {0x0D, 0x05});
            put(4600, new int[] {0x0C, 0xBC});
            put(4700, new int[] {0x0C, 0x77});
            put(4800, new int[] {0x0C, 0x35});
            put(4900, new int[] {0x0B, 0xF5});
            put(5000, new int[] {0x0B, 0xB8});
            put(5100, new int[] {0x0B, 0x7D});
            put(5200, new int[] {0x0B, 0x44});
            put(5300, new int[] {0x0B, 0x0E});
            put(5400, new int[] {0x0A, 0xD9});
            put(5500, new int[] {0x0A, 0xA7});
            put(5600, new int[] {0x0A, 0x76});
            put(5700, new int[] {0x0A, 0x47});
            put(5800, new int[] {0x0A, 0x1A});
            put(5900, new int[] {0x09, 0xEE});
            put(6000, new int[] {0x09, 0xC4});
            put(6100, new int[] {0x09, 0x9B});
            put(6200, new int[] {0x09, 0x73});
            put(6300, new int[] {0x09, 0x4C});
            put(6400, new int[] {0x09, 0x27});
            put(6500, new int[] {0x09, 0x03});
            put(6600, new int[] {0x08, 0xE0});
            put(6700, new int[] {0x08, 0xBE});
            put(6800, new int[] {0x08, 0x9D});
            put(6900, new int[] {0x08, 0x7D});
            put(7000, new int[] {0x08, 0x5E});
            put(7100, new int[] {0x08, 0x40});
            put(7200, new int[] {0x08, 0x23});
            put(7300, new int[] {0x08, 0x06});
            put(7400, new int[] {0x07, 0xEB});
            put(7500, new int[] {0x07, 0xD0});
            put(7600, new int[] {0x07, 0xB5});
            put(7700, new int[] {0x07, 0x9C});
            put(7800, new int[] {0x07, 0x83});
            put(7900, new int[] {0x07, 0x6A});
            put(8000, new int[] {0x07, 0x53});
            //endregion
    }};

    public static boolean BitIsSet(int testbyte, int bitnum) {
        return (testbyte & bitnum) == bitnum;
    }

    public static int PowerOf2(int power) {
        return 1 << power;
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

    public static Integer[] GetAvailTemperatures() {
        List<Integer> tmp = new ArrayList<>();
        for (int aTempMap : tempMap) {
            if (!tmp.contains(aTempMap))
                tmp.add(aTempMap);
        }
        return tmp.toArray(new Integer[tmp.size()]);
    }

    public static int GetTemperatureRaw(int celciusTemp) {
        for (int i = tempMap.length - 1; i >= 0; i--) {
            if (tempMap[i] == celciusTemp)
                return i + 71;
        }
        return 70;
    }

    public static int GetRPMFromRaw(int rawVal) {
        // This is a real magic. Don't ask what does this numbers means.
        // I don't know, i've just get this equation from KME.
        return 50 * RPMAccuracy * (
               (15000064 / rawVal + 25 * RPMAccuracy) / (50 * RPMAccuracy));
    }

    public static Integer[] GetAvailRpms() {
        return rpmMap.keySet().toArray(new Integer[0]);
    }

    public static int[] GetRPMToRaw(int rpm) throws ArrayIndexOutOfBoundsException {
        if (!rpmMap.containsKey(rpm))
            throw new ArrayIndexOutOfBoundsException();
        return rpmMap.get(rpm);
    }

    public static char[] GetRawDate(int year, int month, int day) {
        char ret[] = new char[2];
        day = day - 1;
        year = year - 2000;
        int monthHigh = GetMaskedBytes(month, 8) << 4;
        int monthLow = GetMaskedBytes(month, 7) << 5;
        ret[0] = (char) (monthLow | day);
        ret[1] = (char) (monthHigh | year);
        return ret;
    }

    public static char[] GetRawRunningCounter(int hours, int min) {
        char ret[] = new char[3];
        ret[0] = (char) min;
        ret[2] = (char) (hours / (char)256);
        ret[1] = (char) ( hours - ret[2]*256);
        return ret;
    }

    public static byte[] packFrame(char id, char val) {
        byte[] frameByte = new byte[4];
        frameByte[0] = 0x65;
        frameByte[1] = (byte) id;
        frameByte[2] = (byte) val;
        // This zero here is just for simplyfing CRC
        // generation algorithm
        frameByte[3] = 0;
        frameByte[3] = (byte) BluetoothController.getCRC(frameByte);
        return frameByte;
    }

    public static byte[] packFrame(int id, int val) {
        return packFrame((char) id, (char) val);
    }

}


