package com.mobrembski.kmeviewer.SerialFrames;

public class OtherSettingsFrame extends KMEFrame {
    public OtherSettingsFrame() {
        super.askFrame = new byte[]{0x65, 0x05, 0x05, 0x6F};
        super.answerSize = 20;
    }
}
