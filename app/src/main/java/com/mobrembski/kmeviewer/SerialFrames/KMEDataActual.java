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
        super.askFrame = new byte[]{0x65, 0x02, 0x02, 0x69};
        super.answerSize = 10;
    }

    public KMEDataActual(int[] array) {
        super.askFrame = new byte[]{0x65, 0x02, 0x02, 0x69};
        super.answerSize = 10;
        if (array == null || array.length == 0)
            return;

        TPS = GetVoltage(array[1]);
        Lambda = GetVoltage(array[2]);
        Actuator = array[3];
        PWA = array[4];
        RPM = GetRPM(array[6] << 8 | array[5]);

        LambdaColor = BitUtils.GetMaskedBytes(array[7], 7);
        Ignition = BitUtils.BitIsSet(array[7], 8);
        TPSColor = (BitUtils.GetMaskedBytes(array[7], 240) >> 4);

        WorkingOnGas = BitUtils.BitIsSet(array[8], 1);
        RPMTooHigh = BitUtils.BitIsSet(array[8], 4);
        CutOffActivated = BitUtils.BitIsSet(array[8], 8);
        RPOK = BitUtils.BitIsSet(array[8], 64);
        TemperatureOK = BitUtils.BitIsSet(array[8], 128);

        ActualTemp = BitUtils.GetTemperature(array[9]);
    }
}
