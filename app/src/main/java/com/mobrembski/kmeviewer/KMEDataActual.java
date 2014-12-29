package com.mobrembski.kmeviewer;

public class KMEDataActual extends KMEData {

	public float TPS;
    public int actuator;
    public int actualTemp;
	
	public static KMEDataActual GetDataFromByteArray(int[] array) {
		KMEDataActual nowy = new KMEDataActual();
		
		nowy.TPS = 0+ array[1];
        nowy.actuator = 0+ array[3];
        nowy.actualTemp = 0+ array[9];
		return nowy;
	}
}
