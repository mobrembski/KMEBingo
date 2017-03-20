package com.mobrembski.kmebingo.bluetoothmanager;

import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

interface ISerialConnectionManager {
    void postNewRequest(KMEFrame requestData, int priority);
    void startConnecting();
    void stopConnections();
    int getTransmittedPacketCount();
    int getErrorsCount();
}
