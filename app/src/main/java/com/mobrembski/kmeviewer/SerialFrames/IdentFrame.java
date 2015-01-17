package com.mobrembski.kmeviewer.SerialFrames;

public class IdentFrame extends KMEFrame {
    public IdentFrame() {
        super.answerSize = 3;
        super.askFrame = new byte[]{0x65, 0x01, 0x01, 0x67};
    }
}
