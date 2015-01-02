package com.mobrembski.kmeviewer;

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
    public int PlateLetter1;
    public int PlateLetter2;
    public int PlateLetter3;
    public int PlateLetter4;
    public int PlateLetter5;
    public int PlateLetter6;
    public int PlateLetter7;

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
            dataInfo.hoursOnGas = array[9];
            dataInfo.minutesOnGas = array[10];
            dataInfo.multipliedHoursOnGas = array[11];
            dataInfo.DateOfInstallation = array[12];
            dataInfo.YearOfInstallation = array[13];
            dataInfo.PlateLetter1 = array[14];
            dataInfo.PlateLetter2 = array[15];
            dataInfo.PlateLetter3 = array[16];
            dataInfo.PlateLetter4 = array[17];
            dataInfo.PlateLetter5 = array[18];
            dataInfo.PlateLetter6 = array[19];
            dataInfo.PlateLetter7 = array[20];
        }
        return dataInfo;
    }
}