package com.mobrembski.kmeviewer.SerialFrames;

public class KMEFrame {
    public byte[] askFrame;
    public int answerSize;

    public KMEFrame() {

    }

    public KMEFrame(byte[] customCommandBytes) {
        askFrame = customCommandBytes;
        answerSize = 0;
    }
}