package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.BitUtils;

public class KMEDataInfo extends KMEFrame {
    public int SensorLevel1;
    public int SensorLevel2;
    public int SensorLevel3;
    public int SensorLevel4;
    public int LevelIndicatorOn;
    public int byte6;
    public int byte7;
    public int minutesOnGas;
    public int hoursOnGas;
    public int MonthOfInstallation;
    public int DayOfInstallation;
    public int YearOfInstallation;
    public String RegistrationPlate;

    public KMEDataInfo() {
        super.askFrame = new byte[]{0x65, 0x05, 0x05, 0x6F};
        super.answerSize = 20;
    }

    public static KMEDataInfo GetDataFromByteArray(int[] array) {
        KMEDataInfo dataInfo = new KMEDataInfo();
        if (array != null && array.length > 0) {
            dataInfo.SensorLevel1 = array[1];
            dataInfo.SensorLevel2 = array[2];
            dataInfo.SensorLevel3 = array[3];
            dataInfo.SensorLevel4 = array[4];
            dataInfo.LevelIndicatorOn = BitUtils.GetMaskedBytes(array[5], 7);
            dataInfo.byte6 = array[6];
            dataInfo.byte7 = array[7];
            dataInfo.minutesOnGas = array[8];
            dataInfo.hoursOnGas = array[9] + 256 * array[10];

            int monthLow = (BitUtils.GetMaskedBytes(array[11], 224) >> 5);
            int monthHigh = (BitUtils.GetMaskedBytes(array[12], 128) >> 4);
            dataInfo.MonthOfInstallation = (monthHigh | monthLow) + 1;
            dataInfo.DayOfInstallation = BitUtils.GetMaskedBytes(array[11], 31) + 1;
            dataInfo.YearOfInstallation = BitUtils.GetMaskedBytes(array[12], 127) + 2000;

            StringBuilder sb = new StringBuilder();
            for (int i = 13; i <= 19; i++)
                sb.append((char) array[i]);
            dataInfo.RegistrationPlate = sb.toString();
        }
        return dataInfo;
    }
}