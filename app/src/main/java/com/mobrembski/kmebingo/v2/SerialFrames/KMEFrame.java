package com.mobrembski.kmebingo.v2.SerialFrames;

public abstract class KMEFrame {
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

    public abstract void fillWithData(int[] responseArray);

    public abstract void sendEventWithResponse();
}