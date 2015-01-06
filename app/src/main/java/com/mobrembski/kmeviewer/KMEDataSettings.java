package com.mobrembski.kmeviewer;

public class KMEDataSettings {
    public int LambdaType;
    public int TPSType;
    public int LambdaNeutralPoint;
    public int LambdaDelay;
    public int EmulationType;
    public boolean StartOnGas;
    public int StartOnGasOpenTime;
    public int LambdaEmulationHState;
    public int LambdaEmulationLState;
    public int LPGOnTemperature;
    public int GazBensinTime;
    public int TPSInertial;
    public boolean TurnOnAtIncreasingRPM;


    public static KMEDataSettings GetDataFromByteArray(int[] array) {
        KMEDataSettings dataActual = new KMEDataSettings();
        if (array.length > 0) {
            dataActual.LambdaType = BitUtils.GetMaskedBytes(array[2], 240);
            dataActual.TPSType = BitUtils.GetMaskedBytes(array[2], 15);
            // TODO: Figure out how Neutral Point is coded
            dataActual.LambdaNeutralPoint = array[3];
            dataActual.LambdaDelay = array[4];
            dataActual.EmulationType = BitUtils.GetMaskedBytes(array[5], 3);
            dataActual.StartOnGas = BitUtils.BitIsSet(array[5], 8);
            dataActual.StartOnGasOpenTime = BitUtils.GetMaskedBytes(array[8], 63);
            dataActual.LambdaEmulationHState = array[6];
            dataActual.LambdaEmulationLState = array[7];
            dataActual.LPGOnTemperature = array[11];
            dataActual.GazBensinTime = BitUtils.GetMaskedBytes(array[10], 63);
            dataActual.TPSInertial = array[12];
            dataActual.TurnOnAtIncreasingRPM = BitUtils.BitIsSet(array[1], 16);
        }
        return dataActual;
    }
}
