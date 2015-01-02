package com.mobrembski.kmeviewer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;
import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;
import java.util.concurrent.PriorityBlockingQueue;

public class BluetoothController extends Observable {
    public static PriorityBlockingQueue<AskFrameClass> queue = new PriorityBlockingQueue<AskFrameClass>(1024);
    //private final UUID SERIAL_UUID = UUID
    //	.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final UUID SERIAL_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket socket = null;
    private Thread connectionThread;
    private Thread parseThread;
    private boolean connected = false;
    private long packetsRcv = 0;
    private long packetsError = 0;
    private InputStream inStream;
    private OutputStream outStream;
    private byte[] buffer;

    public BluetoothController(BluetoothDevice device) {
        try {
            socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                _bluetooth.cancelDiscovery();
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket.connect();
                    connected = true;
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                    buffer = new byte[21];
                    inStream = tmpIn;
                    outStream = tmpOut;
                    // TODO: This shouldn't be here...
                    Thread.sleep(1000);
                    if (connected)
                        parseThread.start();
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
        parseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (connected) {
                    if (queue.size() > 0) {
                        try {
                            AskFrameClass askingFrame = queue.take();
                            queue.remove(askingFrame);
                            askForFrame(askingFrame.frame, askingFrame.waiter);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
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

    public int[] askForFrame(KMEFrame frame, PacketReceivedWaiter waiter) {
        try {
            if (connected && outStream != null && inStream != null) {
                outStream.write(frame.askFrame);
                outStream.flush();
                do {
                    if (!connected)
                        waiter.packetReceived(new int[frame.answerSize]);
                }
                while (inStream.available() <= frame.answerSize && connected);
                if (!connected)
                    return null;
                int i = inStream.read(buffer);
                int values[] = checkCRC(buffer, i);
                packetsRcv++;
                if (values != null && waiter != null)
                    waiter.packetReceived(values);
                else {
                    waiter.packetReceived(new int[frame.answerSize]);
                    packetsError++;
                }
                setChanged();
                notifyObservers();
            }
        } catch (IOException e) {
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

    public boolean GetConnected() {
        return connected;
    }

    public void Start() {
        connectionThread.start();
    }

    public void Stop() {
        connected = false;
        try {
            if (parseThread != null)
                parseThread.join();
            if (connectionThread != null)
                connectionThread.join();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
