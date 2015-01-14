package com.mobrembski.kmeviewer.SerialFrames;

public class OtherFrame extends KMEFrame {
    public OtherFrame(byte[] customCommandBytes) {
        super.askFrame = customCommandBytes;
        super.answerSize = 0;
    }
}