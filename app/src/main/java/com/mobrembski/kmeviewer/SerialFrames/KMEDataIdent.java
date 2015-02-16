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
        super.askFrame = new byte[]{0x65, 0x01, 0x01, 0x67};
        super.answerSize = 3;
    }

    public KMEDataIdent(int[] array) {
        super.askFrame = new byte[]{0x65, 0x01, 0x01, 0x67};
        super.answerSize = 3;
        if (array == null || array.length == 0)
            return;

        ControllerType = BitUtils.BitIsSet(array[2], 32) ?
                BingoType.BingoM : BingoType.BingoS;
        Byte1Raw = array[1];
        if (BitUtils.BitIsSet(array[2], 64))
            VersionMajor = ControllerType == BingoType.BingoM ? 4 : 3;
        else
            VersionMajor = ControllerType == BingoType.BingoM ? 2 : 1;
        VersionMinor = BitUtils.GetMaskedBytes(array[2], 31);
        VersionString = String.format("%d.%02d",
                VersionMajor,
                VersionMinor);
    }
}