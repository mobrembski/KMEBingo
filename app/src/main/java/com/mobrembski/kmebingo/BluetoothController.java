package com.mobrembski.kmebingo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.text.format.Time;

import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Observable;

public class BluetoothController extends Observable {
    private static final Object lock = new Object();
    private static boolean connected = false;
    private static volatile BluetoothController instance;
    public final Time StartTime = new Time();
    private final ArrayList<ControllerEvent> connectionListeners = new ArrayList<>();
    private final BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
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
        if (sum == values[frameSize - 1])
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

    public void Connect(final ControllerExceptionEvent cex) {
        if (device == null)
            return;
        try {
            // This seems to be a workaround for Service discovery failed for some devices.
            // See http://stackoverflow.com/questions/3397071 for more details.
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            socket = (BluetoothSocket) m.invoke(device ,1);
        } catch (Exception e) {
            if (cex != null)
                cex.onConnectionException(e.getLocalizedMessage());
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
                    Thread.sleep(1500);
                    notifyOnConnectionStarting();
                } catch (IOException e) {
                    try {
                        socket.close();
                        if (cex != null)
                            cex.onConnectionException(e.getLocalizedMessage());
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
        packetsError = 0;
        packetsRcv = 0;
        try {
            if (connectionThread != null)
                connectionThread.join();
            if (socket != null)
                socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized int[] askForFrame(KMEFrame frame) {
        try {
            if (connected && outStream != null && inStream != null) {
                outStream.write(frame.askFrame);
                outStream.flush();
                int avail;
                do {
                    // For some reason, android not always throws exception
                    // when using inStream on disabled bluetooth adapter.
                    // I don't understand this. It will be fixed when
                    // askForFrame will be rewritten as async task.
                    if (!_bluetooth.isEnabled()) {
                        Disconnect();
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
        } catch (Exception e) {
            e.printStackTrace();
            Disconnect();
            return null;
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

}
