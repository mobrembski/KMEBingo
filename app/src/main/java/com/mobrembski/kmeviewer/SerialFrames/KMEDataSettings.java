package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.BitUtils;

@SuppressWarnings("UnusedDeclaration")
public class KMEDataSettings extends KMEFrame {
    public enum LevelSensorType {
        Reserve,
        Ohm,
        Prog
    }
    public enum LambdaEmulationType {
        Course,
        Ground,
        Disconnected
    }
    public enum EconomyType {
        Normal,
        Eco,
        Sport
    }
    public int LambdaType;
    public int TPSType;
    public int LambdaNeutralPoint;
    public int LambdaDelay;
    public boolean StartOnGas;
    public int StartOnGasOpenTime;
    public int LambdaEmulationHState;
    public int LambdaEmulationLState;
    public int LPGOnTemperature;
    public int GazBensinTime;
    public int TPSInertial;
    public boolean TurnOnAtIncreasingRPM;
    public boolean CutOffEnabled;
    public boolean CutOffHighRPMEnabled;
    public boolean ATTEnabled;
    public boolean TemperatureSensorEnabled;
    public boolean PWAEnabled;
    public boolean ButtonWithLevelIndicator;
    public boolean LowRPMSignalLevel;
    public LevelSensorType LevelSensor;
    public LambdaEmulationType LambdaEmulation;
    public EconomyType DriveType;

    public KMEDataSettings() {
        super.askFrame = new byte[]{0x65, 0x03, 0x03, 0x6B};
        super.answerSize = 13;
    }

    public KMEDataSettings(int[] array) {
        super.askFrame = new byte[]{0x65, 0x03, 0x03, 0x6B};
        super.answerSize = 13;
        if (array == null || array.length == 0)
            return;

        if (BitUtils.BitIsSet(array[1], 1))
            LevelSensor = LevelSensorType.Reserve;
        else if (BitUtils.BitIsSet(array[1], 128))
            LevelSensor = LevelSensorType.Ohm;
        else
            LevelSensor = LevelSensorType.Prog;
        TemperatureSensorEnabled = BitUtils.BitIsSet(array[1], 2);
        PWAEnabled = !BitUtils.BitIsSet(array[1], 4);
        ATTEnabled = BitUtils.BitIsSet(array[1], 8);
        TurnOnAtIncreasingRPM = BitUtils.BitIsSet(array[1], 16);
        CutOffEnabled = BitUtils.BitIsSet(array[1], 32);
        CutOffHighRPMEnabled = BitUtils.BitIsSet(array[1], 64);

        LambdaType = BitUtils.GetMaskedBytes(array[2], 240);
        TPSType = BitUtils.GetMaskedBytes(array[2], 15);

        // TODO: Figure out how Neutral Point is coded
        LambdaNeutralPoint = array[3];
        LambdaDelay = array[4];

        switch (BitUtils.GetMaskedBytes(array[5], 3))
        {
            case 0:
                LambdaEmulation = LambdaEmulationType.Course;
                break;
            case 1:
                LambdaEmulation = KMEDataSettings.LambdaEmulationType.Ground;
                break;
            case 2:
                LambdaEmulation = LambdaEmulationType.Disconnected;
                break;
        }
        LowRPMSignalLevel = BitUtils.BitIsSet(array[5], 4);
        int i = BitUtils.GetMaskedBytes(array[5], 48) >> 4;
        switch (BitUtils.GetMaskedBytes(array[5], 48) >> 4)
        {
            case 0:
                DriveType = EconomyType.Normal;
                break;
            case 1:
                DriveType = EconomyType.Eco;
                break;
            case 2:
                DriveType = EconomyType.Sport;
                break;
        }
        StartOnGas = BitUtils.BitIsSet(array[5], 8);
        LambdaEmulationHState = array[6];
        LambdaEmulationLState = array[7];
        StartOnGasOpenTime = BitUtils.GetMaskedBytes(array[8], 63);
        GazBensinTime = BitUtils.GetMaskedBytes(array[10], 63);
        LPGOnTemperature = BitUtils.GetTemperature(array[11]);
        TPSInertial = array[12];
    }
}
