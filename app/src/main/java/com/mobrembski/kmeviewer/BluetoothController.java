package com.mobrembski.kmeviewer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.format.Time;

import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.UUID;

public class BluetoothController extends Observable {
    public Time StartTime = new Time();
    private static boolean connected = false;
    private static volatile Object lock = new Object();
    private static volatile BluetoothController instance;
    //private final UUID SERIAL_UUID = UUID
    //	.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final UUID SERIAL_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ArrayList<ControllerEvent> connectionListeners = new ArrayList<ControllerEvent>();
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice device = null;
    private BluetoothSocket socket = null;
    private InputStream inStream;
    private OutputStream outStream;
    private Thread connectionThread;
    private long packetsRcv = 0;
    private long packetsError = 0;
    private byte[] buffer;

    private BluetoothController() {

    }

    public static BluetoothController getInstance() {
        BluetoothController tmpInst = instance;
        if (tmpInst == null) {
            synchronized (lock) {
                tmpInst = instance;
                if (tmpInst == null) {
                    tmpInst = new BluetoothController();
                    instance = tmpInst;
                }
            }
        }
        return tmpInst;
    }

    private static int[] checkCRC(byte[] receivedFrame, int frameSize) {
        int values[] = new int[receivedFrame.length];
        int sum = 0;
        for (int j = 0; j < frameSize; j++) {
            values[j] = (receivedFrame[j] & 0xff);
            if (j < frameSize - 1)
                sum += values[j];
        }
        sum = sum & 0xFF;
        if (sum == values[frameSize - 1] && sum != 0)
            return values;
        return null;
    }

    public static int getCRC(byte[] frameByte) {
        int sum = 0;
        for (int j = 0; j < frameByte.length; j++) {
            if (j < frameByte.length - 1)
                sum += (frameByte[j] & 0xff);
        }
        sum = sum & 0xFF;
        return sum;
    }

    public void AddOnConnectionListener(ControllerEvent evt) {
        connectionListeners.add(evt);
    }

    public void RemoveOnConnectionListener(ControllerEvent evt) {
        connectionListeners.remove(evt);
    }

    public void RemoveAllListeners() {
        connectionListeners.clear();
    }

    private void notifyOnConnectionStopping() {
        for (ControllerEvent ee : connectionListeners)
            ee.onConnectionStopping();
    }

    private void notifyOnConnectionStarting() {
        for (ControllerEvent ee : connectionListeners)
            ee.onConnectionStarting();
    }

    public void Connect() {
        if (device == null)
            return;
        try {
            socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                _bluetooth.cancelDiscovery();
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket.connect();
                    connected = true;
                    buffer = new byte[21];
                    inStream = socket.getInputStream();
                    outStream = socket.getOutputStream();
                    // TODO: This shouldn't be here...
                    StartTime.setToNow();
                    Thread.sleep(1000);
                    notifyOnConnectionStarting();
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        connectionThread.start();
    }

    public void Disconnect() {
        deleteObservers();
        notifyOnConnectionStopping();
        connected = false;
        try {
            if (connectionThread != null)
                connectionThread.join();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized int[] askForFrame(KMEFrame frame) {
        try {
            if (connected && outStream != null && inStream != null) {
                outStream.write(frame.askFrame);
                outStream.flush();
                int avail = 0;
                do {
                    if (!connected) {
                        return new int[frame.answerSize];
                    }
                    // TODO: remove this sleep. Added just to free some
                    // cpu time
                    if (frame.answerSize <= 0)
                        return null;
                    Thread.sleep(10);
                    avail = inStream.available();
                }
                while (avail <= frame.answerSize && connected);
                if (!connected)
                    return null;
                int i = inStream.read(buffer);
                int values[] = checkCRC(buffer, i);
                packetsRcv++;
                setChanged();
                notifyObservers();
                if (values == null)
                    packetsError++;
                return values;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long GetRecvPacketsCount() {
        return packetsRcv;
    }

    public long GetErrorsCount() {
        return packetsError;
    }

    public boolean IsConnected() {
        return connected;
    }

    public void SetDevice(BluetoothDevice device) {
        this.device = device;
    }

    @Override
    public synchronized boolean hasChanged() {
        return super.hasChanged();
    }

    @Override
    public synchronized void notifyObservers() {
        super.notifyObservers();
    }

}
