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

    public static KMEDataSettings GetDataFromByteArray(int[] array) {
        KMEDataSettings dataActual = new KMEDataSettings();
        if (array != null && array.length > 0) {
            if (BitUtils.BitIsSet(array[1], 1))
                dataActual.LevelSensor = LevelSensorType.Reserve;
            else if (BitUtils.BitIsSet(array[1], 128))
                dataActual.LevelSensor = LevelSensorType.Ohm;
            else
                dataActual.LevelSensor = LevelSensorType.Prog;
            dataActual.TemperatureSensorEnabled = BitUtils.BitIsSet(array[1], 2);
            dataActual.PWAEnabled = !BitUtils.BitIsSet(array[1], 4);
            dataActual.ATTEnabled = BitUtils.BitIsSet(array[1], 8);
            dataActual.TurnOnAtIncreasingRPM = BitUtils.BitIsSet(array[1], 16);
            dataActual.CutOffEnabled = BitUtils.BitIsSet(array[1], 32);
            dataActual.CutOffHighRPMEnabled = BitUtils.BitIsSet(array[1], 64);

            dataActual.LambdaType = BitUtils.GetMaskedBytes(array[2], 240);
            dataActual.TPSType = BitUtils.GetMaskedBytes(array[2], 15);

            // TODO: Figure out how Neutral Point is coded
            dataActual.LambdaNeutralPoint = array[3];
            dataActual.LambdaDelay = array[4];

            switch (BitUtils.GetMaskedBytes(array[5], 3))
            {
                case 0:
                    dataActual.LambdaEmulation = LambdaEmulationType.Course;
                    break;
                case 1:
                    dataActual.LambdaEmulation = KMEDataSettings.LambdaEmulationType.Ground;
                    break;
                case 2:
                    dataActual.LambdaEmulation = LambdaEmulationType.Disconnected;
                    break;
            }
            dataActual.LowRPMSignalLevel = BitUtils.BitIsSet(array[5], 4);
            int i = BitUtils.GetMaskedBytes(array[5], 48) >> 4;
            switch (BitUtils.GetMaskedBytes(array[5], 48) >> 4)
            {
                case 0:
                    dataActual.DriveType = EconomyType.Normal;
                    break;
                case 1:
                    dataActual.DriveType = EconomyType.Eco;
                    break;
                case 2:
                    dataActual.DriveType = EconomyType.Sport;
                    break;
            }
            dataActual.StartOnGas = BitUtils.BitIsSet(array[5], 8);
            dataActual.LambdaEmulationHState = array[6];
            dataActual.LambdaEmulationLState = array[7];
            dataActual.StartOnGasOpenTime = BitUtils.GetMaskedBytes(array[8], 63);
            dataActual.GazBensinTime = BitUtils.GetMaskedBytes(array[10], 63);
            dataActual.LPGOnTemperature = BitUtils.GetTemperature(array[11]);
            dataActual.TPSInertial = array[12];
        }
        return dataActual;
    }
}
