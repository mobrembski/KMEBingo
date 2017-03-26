package com.mobrembski.kmebingo.bluetoothmanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class BluetoothConnectRunnable implements Runnable  {

    private BluetoothAdapter _bluetooth;
    private BluetoothSocket socket = null;
    private BluetoothDevice device;
    private String deviceAddress;
    private IConnectionStarted connectionStartedListener;
    // bug? isConnected from socket always returns false somehow...
    private boolean isConnected = false;
    private SerialConnectionStatusEvent.SerialConnectionStatus currentSerialLineStatus;

    BluetoothConnectRunnable(IConnectionStarted connectionStartedListener, String deviceAddress) {
        this._bluetooth = BluetoothAdapter.getDefaultAdapter();
        this.connectionStartedListener = connectionStartedListener;
        this.currentSerialLineStatus =
                SerialConnectionStatusEvent.SerialConnectionStatus.DISCONNECTED;
        this.deviceAddress = deviceAddress;
    }

    @Override
    public void run() {
        if (_bluetooth == null) return;
        if (!_bluetooth.isEnabled()) return;
        if (isConnected) return;

        saveStatusAndEmitEvent(
                SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTING);
        Log.d("DebugBT", "Trying to get device. Address is: " + deviceAddress);
        device = _bluetooth.getRemoteDevice(deviceAddress);
        _bluetooth.cancelDiscovery();
        if (device == null) return;
        try {
            // This seems to be a workaround for Service discovery failed for some devices.
            // See http://stackoverflow.com/questions/3397071 for more details.
            Log.d("DebugBT", "Trying to get bluetoothSocket");
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            socket = (BluetoothSocket) m.invoke(device, 1);
        } catch (Exception nos) {
            Log.e("DebugBT","Cannot find createRfcommSocket??");
            return;
        }

        Log.d("DebugBT","startConnecting thread class invoked");
        Log.d("DebugBT","Discovery canceled.");
        ExecutorService socketConnectService = Executors.newSingleThreadExecutor();
        Future<?> connectingFuture = socketConnectService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    Log.d("DebugBT","Trying to get bluetoothSocket...");
                    socket.connect();
                    Log.d("DebugBT","Got connection!");
                    isConnected = true;
                    connectionStartedListener.onConnectionStarted(socket);
                    saveStatusAndEmitEvent(
                            SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED);
                } catch (IOException e) {
                    Log.d("DebugBT", "exception 1:"+ e.getClass().getCanonicalName()+e.getMessage());
                    closeSocket();
                }
            }
        });
        try {
            connectingFuture.get(8, TimeUnit.SECONDS);
        } catch (Exception ex) {
            Log.d("DebugBT", "Exception future:",ex);
            closeSocket();
        }
        connectingFuture.cancel(true);
        socketConnectService.shutdownNow();
    }

    private void saveStatusAndEmitEvent(SerialConnectionStatusEvent.SerialConnectionStatus status) {
        this.currentSerialLineStatus = status;
        EventBus.getDefault().post(new SerialConnectionStatusEvent(currentSerialLineStatus));
    }

    public SerialConnectionStatusEvent.SerialConnectionStatus getConnectionStatus() {
        return currentSerialLineStatus;
    }


    public void closeSocket() {
        try {
            socket.getInputStream().close();
            socket.getOutputStream().close();
            socket.close();
            isConnected = false;
            Log.d("DebugBT", "closing bluetoothSocket due to exception");
            saveStatusAndEmitEvent(
                    SerialConnectionStatusEvent.SerialConnectionStatus.DISCONNECTED);
        } catch (IOException ioe) {

        }
    }

    public boolean isConnected() {
        return isConnected;
    }
}
