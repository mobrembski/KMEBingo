package com.mobrembski.kmebingo.SerialFrames;

public class KMEFrame {
    public byte[] askFrame;
    public int answerSize;

    public KMEFrame() {

    }

    public KMEFrame(byte[] customCommandBytes) {
        askFrame = customCommandBytes;
        answerSize = 0;
    }

    public KMEFrame(byte[] customCommandBytes, int expectedAnswerSize) {
        askFrame = customCommandBytes;
        answerSize = expectedAnswerSize;
    }
}