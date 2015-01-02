package com.mobrembski.kmeviewer;

import java.nio.channels.spi.AbstractSelectionKey;

public class KMEDataInfo {
    public int sensorLevel1;
    public int sensorLevel2;
    public int sensorLevel3;
    public int sensorLevel4;
    public int byte5;
    public int byte6;
    public int byte7;
    public int minutesOnGas;
    public int hoursOnGas;
    public int multipliedHoursOnGas;
    public int DateOfInstallation;
    public int YearOfInstallation;
    public String RegistrationPlate;

    public static KMEDataInfo GetDataFromByteArray(int[] array) {
        KMEDataInfo dataInfo = new KMEDataInfo();
        if (array.length > 0) {
            dataInfo.sensorLevel1 = array[1];
            dataInfo.sensorLevel2 = array[2];
            dataInfo.sensorLevel3 = array[3];
            dataInfo.sensorLevel4 = array[4];
            dataInfo.byte5 = array[5];
            dataInfo.byte6 = array[6];
            dataInfo.byte7 = array[7];
            dataInfo.minutesOnGas = array[8];
            dataInfo.hoursOnGas = array[9]+256*array[10];
            dataInfo.multipliedHoursOnGas = array[10];
            dataInfo.DateOfInstallation = array[11];
            dataInfo.YearOfInstallation = array[12];
            StringBuilder sb = new StringBuilder();
            for (int i=13;i<=19;i++)
                sb.append((char)array[i]);
            dataInfo.RegistrationPlate=sb.toString();
        }
        return dataInfo;
    }
}