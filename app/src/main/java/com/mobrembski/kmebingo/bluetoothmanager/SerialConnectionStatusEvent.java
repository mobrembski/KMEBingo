package com.mobrembski.kmebingo.bluetoothmanager;

public class SerialConnectionStatusEvent {
    public enum SerialConnectionStatus {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ADAPTER_OFF
    }

    public SerialConnectionStatus currentStatus;

    public SerialConnectionStatusEvent(SerialConnectionStatus status) {
        this.currentStatus = status;
    }
}
