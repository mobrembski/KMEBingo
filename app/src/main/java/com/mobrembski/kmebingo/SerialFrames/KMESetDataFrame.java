package com.mobrembski.kmebingo.SerialFrames;

public class KMESetDataFrame extends KMEFrame {

    public KMESetDataFrame(byte[] requestArray, int answerSize) {
        super.askFrame = requestArray;
        super.answerSize = answerSize;
    }

    @Override
    public void fillWithData(int[] responseArray) { }

    @Override
    public void sendEventWithResponse() { }
}
