package com.mobrembski.kmeviewer;

public class KMEDataIdent {
    public int Byte1Raw;
    public int VersionMajor;
    public int VersionMinor;
    public String VersionString;

    public static KMEDataIdent GetDataFromByteArray(int[] array) {
        KMEDataIdent dataIdent = new KMEDataIdent();
        if (array.length > 0) {
            dataIdent.Byte1Raw = array[1];
            if (BitUtils.BitIsSet(array[2], 64))
                dataIdent.VersionMajor = 3;
            else
                dataIdent.VersionMinor = 1;
            dataIdent.VersionMinor = BitUtils.GetMaskedBytes(array[2], 63);
            dataIdent.VersionString = String.format("%d.%02d",
                    dataIdent.VersionMajor,
                    dataIdent.VersionMinor);
        }
        return dataIdent;
    }
}