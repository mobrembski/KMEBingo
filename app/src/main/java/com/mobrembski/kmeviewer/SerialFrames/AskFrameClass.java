package com.mobrembski.kmeviewer.SerialFrames;

import com.mobrembski.kmeviewer.PacketReceivedWaiter;

public class AskFrameClass implements Comparable<AskFrameClass> {
    public KMEFrame frame;
    public PacketReceivedWaiter waiter;

    public AskFrameClass(KMEFrame frame, PacketReceivedWaiter waiter) {
        this.frame = frame;
        this.waiter = waiter;
    }

    @Override
    public int compareTo(AskFrameClass askFrameClass) {
        return askFrameClass.frame.priority - this.frame.priority;
    }
}
