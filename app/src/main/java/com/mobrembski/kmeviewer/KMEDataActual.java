package com.mobrembski.kmeviewer;

public class KMEDataActual extends KMEData {

	public float TPS;
    public int actuator;
    public int actualTemp;
	
	public static KMEDataActual GetDataFromByteArray(int[] array) {
		KMEDataActual nowy = new KMEDataActual();
		
		nowy.TPS = 0+ ((float) Math.floor((array[1] * (float)0.0196)*100))/100;
        nowy.actuator = 0+ array[3];
        nowy.actualTemp = 0+ array[9];
		return nowy;
	}
}
