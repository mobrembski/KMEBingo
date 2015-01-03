package com.mobrembski.kmeviewer.SerialFrames;

public class SettingsFrame extends KMEFrame {
    public SettingsFrame() {
        super.askFrame = new byte[]{0x65, 0x03, 0x03, 0x6B};
        super.answerSize = 13;
        super.priority = 1;
    }
}
