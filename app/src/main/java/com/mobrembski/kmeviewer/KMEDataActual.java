package com.mobrembski.kmeviewer;

public class KMEDataActual {
    public int TPS;
    public int Lambda;
    public int Actuator;
    public int ActualTemp;
    public int PWA;
    public int RPMRaw1;
    public int RPMRaw2;
    public boolean WorkingOnGas;
    public boolean CutOffActivated;
    public boolean TemperatureOK;
    public boolean RPOK;
    public boolean Ignition;
    public int LambdaColor;
    public int TPSColor;

    public static KMEDataActual GetDataFromByteArray(int[] array) {
        KMEDataActual dataActual = new KMEDataActual();
        if (array.length > 0) {
            dataActual.TPS = array[1];
            dataActual.Lambda = array[2];
            dataActual.Actuator = array[3];
            dataActual.PWA = array[4];
            dataActual.RPMRaw1 = array[5];
            dataActual.RPMRaw2 = array[6];

            dataActual.WorkingOnGas = BitUtils.BitIsSet(array[8], 1);
            dataActual.CutOffActivated = BitUtils.BitIsSet(array[8], 8);
            dataActual.TemperatureOK = BitUtils.BitIsSet(array[8], 128);
            dataActual.Ignition = BitUtils.BitIsSet(array[7], 8);
            dataActual.LambdaColor = BitUtils.GetMaskedBytes(array[7], 7);
            dataActual.TPSColor = BitUtils.GetMaskedBytes(array[7], 240);

            dataActual.RPOK = BitUtils.BitIsSet(array[8], 64);
            dataActual.ActualTemp = array[9];
        }
        return dataActual;
    }
}
