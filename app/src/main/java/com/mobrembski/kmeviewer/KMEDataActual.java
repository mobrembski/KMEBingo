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
            dataActual.TPS = 0 + array[1];
            dataActual.Lambda = 0 + array[2];
            dataActual.Actuator = 0 + array[3];
            dataActual.PWA = 0 + array[4];
            dataActual.RPMRaw1 = array[5];
            dataActual.RPMRaw2 = array[6];

            dataActual.WorkingOnGas = (array[8] & 1) == 1;
            dataActual.CutOffActivated = (array[8] & 8) == 8;
            dataActual.TemperatureOK = (array[8] & 128) == 128;
            dataActual.Ignition = (array[7] & 8) == 8;
            dataActual.LambdaColor = (array[7] & 7);
            dataActual.TPSColor = (array[7] & 240);

            dataActual.RPOK = (array[8] & 64) == 64;
            dataActual.ActualTemp = 0 + array[9];
        }
        return dataActual;
    }
}
