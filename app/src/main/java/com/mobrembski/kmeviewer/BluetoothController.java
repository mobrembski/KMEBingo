package com.mobrembski.kmeviewer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class BluetoothController {

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();
	private BluetoothSocket socket = null;
	private BluetoothDevice device = null;
	
	private Thread connectionThread;
    private Thread parseThread;
	
	public boolean toRun = true;
	public KMEDataActual DataActual = new KMEDataActual();
	private boolean connected = false;
    private Handler _handle;
    private static long packetsRcv=0;
    private static long packetsError=0;
	
	private InputStream inStream;
	private OutputStream outStream;
	
	private ArrayList<KMEDataChanged> eventObservers = new ArrayList<KMEDataChanged>();
	
	public void onKMEDataChanged(KMEDataChanged observer) {
		eventObservers.add(observer);
	}

    //private final UUID MY_UUID = UUID
    //	.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final UUID MY_UUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    byte[] buffer;
    byte[] askFrame = new byte[]{0x65,0x02,0x02,0x69};
	
	public BluetoothController(BluetoothDevice device, final Handler mHandler) {
		this.device = device;
        this._handle = mHandler;
        // Get a BluetoothSocket for a connection with the given BluetoothDevice
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connectionThread  = new Thread(new Runnable() {
            @Override
            public void run() {
                // Always cancel discovery because it will slow down a connection
                _bluetooth.cancelDiscovery();

                // Make a connection to the BluetoothSocket
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket.connect();
                } catch (IOException e) {
                    //connection to device failed so close the socket
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

                // Get the BluetoothSocket input and output streams
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
                while (true) {
                    try {
                        outStream.write(askFrame);
                        outStream.flush();
                        Thread.sleep(50);
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
                            mHandler.obtainMessage(1,i, -1, values).sendToTarget();
                        else
                            packetsError++;
                        mHandler.obtainMessage(2,packetsError).sendToTarget();
                        mHandler.obtainMessage(3,packetsRcv).sendToTarget();
                    } catch (IOException e) {
                        break;
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });

	}

    public void Start() {
        connectionThread.start();
        parseThread.start();
    }

    public void Stop() {
        if(parseThread!=null)
            parseThread.interrupt();
        if(connectionThread!=null)
            connectionThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
