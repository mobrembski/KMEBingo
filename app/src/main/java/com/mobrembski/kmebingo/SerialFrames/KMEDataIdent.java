package com.mobrembski.kmebingo.SerialFrames;

import com.mobrembski.kmebingo.BitUtils;

import org.greenrobot.eventbus.EventBus;

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

    @Override
    public void fillWithData(int[] array) {
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

    @Override
    public void sendEventWithResponse() {
        EventBus.getDefault().post(this);
    }

    public KMEDataIdent(int[] array) {
        super.askFrame = new byte[]{0x65, 0x01, 0x01, 0x67};
        super.answerSize = 3;
        fillWithData(array);
    }
}