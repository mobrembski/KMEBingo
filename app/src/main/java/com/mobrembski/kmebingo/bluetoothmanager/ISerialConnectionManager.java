package com.mobrembski.kmebingo.bluetoothmanager;

import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

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
