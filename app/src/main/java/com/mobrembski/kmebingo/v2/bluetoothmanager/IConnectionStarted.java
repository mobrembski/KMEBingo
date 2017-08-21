package com.mobrembski.kmebingo.v2.bluetoothmanager;

import android.bluetooth.BluetoothSocket;

interface IConnectionStarted {
    void onConnectionStarted(BluetoothSocket socket);
}
