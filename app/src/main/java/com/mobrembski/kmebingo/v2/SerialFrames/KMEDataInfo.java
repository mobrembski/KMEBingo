package com.mobrembski.kmebingo.v2.SerialFrames;

import com.mobrembski.kmebingo.v2.BitUtils;

import org.greenrobot.eventbus.EventBus;

@SuppressWarnings("UnusedDeclaration")
public class KMEDataInfo extends KMEFrame {
    public int SensorLevel1;
    public int SensorLevel2;
    public int SensorLevel3;
    public int SensorLevel4;
    public int LevelIndicatorOn;
    public int minutesOnGas;
    public int hoursOnGas;
    public int MonthOfInstallation;
    public int DayOfInstallation;
    public int YearOfInstallation;
    public String RegistrationPlate;
    public SettingsItem ActuatorSpeedIdleClosingCorrection = new SettingsItem(4,4);
    public SettingsItem ActuatorSpeedIdleOpeningCorrection = new SettingsItem(4,0);
    public SettingsItem ActuatorSpeedLoadClosingCorrection = new SettingsItem(4,4);
    public SettingsItem ActuatorSpeedLoadOpeningCorrection = new SettingsItem(4,0);
    private SettingsRow[] rows = new SettingsRow[2];

    public KMEDataInfo() {
        super.askFrame = new byte[]{0x65, 0x05, 0x05, 0x6F};
        super.answerSize = 20;
        rows[0] = SettingsRow.makeSettingsRow(new SettingsItem[]{
                ActuatorSpeedIdleOpeningCorrection, ActuatorSpeedIdleClosingCorrection});
        rows[1] = SettingsRow.makeSettingsRow(new SettingsItem[]{
                ActuatorSpeedLoadOpeningCorrection, ActuatorSpeedLoadClosingCorrection});
    }

    @Override
    public void fillWithData(int[] array) {
        if (array == null || array.length == 0)
            return;

        SensorLevel1 = array[1];
        SensorLevel2 = array[2];
        SensorLevel3 = array[3];
        SensorLevel4 = array[4];
        LevelIndicatorOn = BitUtils.GetMaskedBytes(array[5], 7);
        rows[0].SetFromRawByte(array[6]);
        rows[1].SetFromRawByte(array[7]);
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

    @Override
    public void sendEventWithResponse() {
        EventBus.getDefault().post(this);
    }

    public KMEDataInfo(int[] array) {
        this();
        fillWithData(array);
    }
}