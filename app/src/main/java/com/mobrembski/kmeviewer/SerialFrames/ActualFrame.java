package com.mobrembski.kmeviewer.SerialFrames;

public class ActualFrame extends KMEFrame {
    public ActualFrame() {
        super.answerSize = 10;
        super.askFrame = new byte[]{0x65, 0x02, 0x02, 0x69};
    }
}
