package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.BitUtils;

@SuppressWarnings("UnusedDeclaration")
public class KMEDataInfo extends KMEFrame {
    public int SensorLevel1;
    public int SensorLevel2;
    public int SensorLevel3;
    public int SensorLevel4;
    public int LevelIndicatorOn;
    public int ActuatorSpeedIdleClosingCorrection;
    public int ActuatorSpeedIdleOpeningCorrection;
    public int ActuatorSpeedLoadClosingCorrection;
    public int ActuatorSpeedLoadOpeningCorrection;
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

    public KMEDataInfo(int[] array) {
        super.askFrame = new byte[]{0x65, 0x05, 0x05, 0x6F};
        super.answerSize = 20;
        if (array == null || array.length == 0)
            return;

        SensorLevel1 = array[1];
        SensorLevel2 = array[2];
        SensorLevel3 = array[3];
        SensorLevel4 = array[4];
        LevelIndicatorOn = BitUtils.GetMaskedBytes(array[5], 7);
        ActuatorSpeedIdleClosingCorrection = BitUtils.GetMaskedBytes(array[6], 240);
        ActuatorSpeedIdleOpeningCorrection = BitUtils.GetMaskedBytes(array[6], 15);
        ActuatorSpeedLoadClosingCorrection = BitUtils.GetMaskedBytes(array[7], 240);
        ActuatorSpeedLoadClosingCorrection = BitUtils.GetMaskedBytes(array[7], 15);
        minutesOnGas = array[8];
        hoursOnGas = array[9] + 256 * array[10];

        int monthLow = (BitUtils.GetMaskedBytes(array[11], 224) >> 5);
        int monthHigh = (BitUtils.GetMaskedBytes(array[12], 128) >> 4);
        MonthOfInstallation = (monthHigh | monthLow) + 1;
        DayOfInstallation = BitUtils.GetMaskedBytes(array[11], 31) + 1;
        YearOfInstallation = BitUtils.GetMaskedBytes(array[12], 127) + 2000;

        StringBuilder sb = new StringBuilder();
        for (int i = 13; i <= 19; i++)
            sb.append((char) array[i]);
        RegistrationPlate = sb.toString();
    }
}