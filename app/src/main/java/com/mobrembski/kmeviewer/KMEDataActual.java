package com.mobrembski.kmeviewer;

public class KMEDataActual extends KMEData {
    public float TPS;
    public int actuator;
    public int actualTemp;

    public static KMEDataActual GetDataFromByteArray(int[] array) {
        KMEDataActual dataActual = new KMEDataActual();
        if (array.length > 0) {
            dataActual.TPS = 0 + array[1];
            dataActual.actuator = 0 + array[3];
            dataActual.actualTemp = 0 + array[9];
        }
        return dataActual;
    }
}
