package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.BitUtils;

@SuppressWarnings("WeakerAccess, UnusedDeclaration")
public class KMEDataIdent extends KMEFrame {
    public enum BingoType {
        BingoS,
        BingoM
    }

    public int Byte1Raw;
    public int VersionMajor;
    public int VersionMinor;
    public String VersionString;
    public BingoType ControllerType;

    public KMEDataIdent() {
        super.answerSize = 3;
        super.askFrame = new byte[]{0x65, 0x01, 0x01, 0x67};
    }

    public static KMEDataIdent GetDataFromByteArray(int[] array) {
        KMEDataIdent dataIdent = new KMEDataIdent();
        if (array != null && array.length > 0) {
            dataIdent.ControllerType = BitUtils.BitIsSet(array[2], 32) ?
                    BingoType.BingoM : BingoType.BingoS;
            dataIdent.Byte1Raw = array[1];
            if (BitUtils.BitIsSet(array[2], 64))
                dataIdent.VersionMajor = dataIdent.ControllerType == BingoType.BingoM ? 4 : 3;
            else
                dataIdent.VersionMajor = dataIdent.ControllerType == BingoType.BingoM ? 2 : 1;
            dataIdent.VersionMinor = BitUtils.GetMaskedBytes(array[2], 31);
            dataIdent.VersionString = String.format("%d.%02d",
                    dataIdent.VersionMajor,
                    dataIdent.VersionMinor);
        }
        return dataIdent;
    }
}