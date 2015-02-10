package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.BitUtils;

import static com.mobrembski.kmeviewer.BitUtils.GetRPM;
import static com.mobrembski.kmeviewer.BitUtils.GetVoltage;

public class KMEDataActual extends KMEFrame {
    public float TPS;
    public float Lambda;
    public int Actuator;
    public int ActualTemp;
    public int PWA;
    public boolean WorkingOnGas;
    public boolean CutOffActivated;
    public boolean TemperatureOK;
    public boolean RPOK;
    public boolean RPMTooHigh;
    public boolean Ignition;
    public int LambdaColor;
    public int TPSColor;
    public int RPM;

    public KMEDataActual() {
        super.answerSize = 10;
        super.askFrame = new byte[]{0x65, 0x02, 0x02, 0x69};
    }

    public static KMEDataActual GetDataFromByteArray(int[] array) {
        KMEDataActual dataActual = new KMEDataActual();
        if (array != null && array.length > 0) {
            dataActual.TPS = GetVoltage(array[1]);
            dataActual.Lambda = GetVoltage(array[2]);
            dataActual.Actuator = array[3];
            dataActual.PWA = array[4];
            dataActual.RPM = GetRPM(array[6] << 8 | array[5]);

            dataActual.LambdaColor = BitUtils.GetMaskedBytes(array[7], 7);
            dataActual.Ignition = BitUtils.BitIsSet(array[7], 8);
            dataActual.TPSColor = (BitUtils.GetMaskedBytes(array[7], 240) >> 4);

            dataActual.WorkingOnGas = BitUtils.BitIsSet(array[8], 1);
            dataActual.RPMTooHigh = BitUtils.BitIsSet(array[8], 4);
            dataActual.CutOffActivated = BitUtils.BitIsSet(array[8], 8);
            dataActual.RPOK = BitUtils.BitIsSet(array[8], 64);
            dataActual.TemperatureOK = BitUtils.BitIsSet(array[8], 128);

            dataActual.ActualTemp = BitUtils.GetTemperature(array[9]);
        }
        return dataActual;
    }
}
