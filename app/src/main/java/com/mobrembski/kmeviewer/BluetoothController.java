package com.mobrembski.kmeviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothController extends Observable {

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	private BluetoothSocket socket = null;
	
	private Thread connectionThread;
    private Thread parseThread;

	private boolean connected = false;
    private long packetsRcv=0;
    private long packetsError=0;
	
	private InputStream inStream;
	private OutputStream outStream;

    //private final UUID MY_UUID = UUID
    //	.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    byte[] buffer;
    byte[] askFrame = new byte[]{0x65,0x02,0x02,0x69};
    private static KMEDataActual actualParam = new KMEDataActual();
	
	public BluetoothController(BluetoothDevice device) {
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionThread  = new Thread(new Runnable() {
            @Override
            public void run() {
                _bluetooth.cancelDiscovery();

                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket.connect();
                    connected = true;
                } catch (IOException e) {
                    try {
                        socket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });
        parseThread  = new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                    buffer = new byte[11];
                    Thread.sleep(1000);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                inStream = tmpIn;
                outStream = tmpOut;
                while (connected) {
                    try {
                        outStream.write(askFrame);
                        outStream.flush();
                        do {
                            if(!connected)
                                break;
                        }
                        while(inStream.available() <=10);
                        if(!connected)
                            break;
                        int i = inStream.read(buffer);
                        int sum=0;
                        int values[] = new int[buffer.length];
                        packetsRcv++;
                        for(int j=0;j<i;j++) {
                            values[j] =( buffer[j] & 0xff);
                            if(j<i-1)
                                sum+=values[j];
                        }
                        sum=sum&0xFF;
                        if(sum==values[values.length-1] && sum!=0)
                            actualParam = actualParam.GetDataFromByteArray(values);
                        else
                            packetsError++;
                        setChanged();
                        notifyObservers();

                    } catch (IOException e) {
                        break;
                    }
                }
            }
        });
	}

    public KMEDataActual GetActualParameters() {
        return actualParam;
    }

    public long GetRecvPacketsCount() {
        return packetsRcv;
    }

    public long GetErrorsCount() {
        return packetsError;
    }

    public void Start() {
        connectionThread.start();
        parseThread.start();
    }

    public void Stop() {
        connected=false;
        try {
            if(parseThread!=null)
                parseThread.join();
            if(connectionThread!=null)
                connectionThread.join();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override public synchronized boolean hasChanged() { return super.hasChanged(); }
    @Override public synchronized void notifyObservers() { super.notifyObservers(); }
}
