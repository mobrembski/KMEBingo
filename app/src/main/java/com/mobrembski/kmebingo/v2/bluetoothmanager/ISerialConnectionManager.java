package com.mobrembski.kmebingo.v2.bluetoothmanager;

import com.mobrembski.kmebingo.v2.SerialFrames.KMEFrame;

public interface ISerialConnectionManager {
    <T extends KMEFrame> void postNewRequest(final T requestData, int priority);
    <T extends KMEFrame> T runRequestNow(final T requestData);
    SerialConnectionStatusEvent.SerialConnectionStatus getConnectionStatus();
    boolean isConnected();
    void startConnecting();
    void stopConnections();
    int getTransmittedPacketCount();
    int getErrorsCount();
    long getConnectedTime();
}
