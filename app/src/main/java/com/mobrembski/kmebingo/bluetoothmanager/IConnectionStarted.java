package com.mobrembski.kmebingo.bluetoothmanager;

import android.bluetooth.BluetoothSocket;

interface IConnectionStarted {
    void onConnectionStarted(BluetoothSocket socket);
}
