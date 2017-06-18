package com.mobrembski.kmebingo.bluetoothmanager;

import android.util.Log;

import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

class BluetoothCommandJob<T extends KMEFrame> implements Callable<T> {
    private BluetoothConnectionManager connectionManager;
    private T requestData;
    String JobName;

    public BluetoothCommandJob(T requestData, int priority, BluetoothConnectionManager manager) {
        this.requestData = requestData;
        this.connectionManager = manager;
        this.JobName = requestData.getClass().getCanonicalName();
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

    @Override
    public T call() throws Exception {
        InputStream inStream;
        OutputStream outStream;
        try {
            Log.d("DebugBT", "Starting from " + requestData.getClass().getSimpleName());
            inStream = connectionManager.getInputStream();
            outStream = connectionManager.getOutputStream();
            if (inStream == null || outStream == null) {
                Log.v("DebugBT", "Finishing because Streams are not ready");
                return null;
            }
            byte[] responseBytes = new byte[requestData.answerSize + 1];
            outStream.write(requestData.askFrame);
            outStream.flush();
            Log.v("DebugBT", "Request was send: " + String.format("0x%02x, 0x%02x, 0x%02x",
                    requestData.askFrame[0], requestData.askFrame[1], requestData.askFrame[2]));
            int responseBytesPtr = 0;
            byte[] tmp = new byte[1];
            while (responseBytesPtr < (requestData.answerSize + 1)) {
                inStream.read(tmp, 0, 1);
                responseBytes[responseBytesPtr++] = tmp[0];
                Log.v("DebugBT",
                        String.format("Got %d/%d byte for req %s. Value is %02x",
                                responseBytesPtr, (requestData.answerSize + 1),
                                requestData.getClass().getSimpleName(), tmp[0]));
            }

            //int i = inStream.read(responseBytes);
            int values[] = checkCRC(responseBytes, responseBytesPtr);
            Log.d("DebugBT", "Finishing from " + requestData.getClass().getSimpleName());
            requestData.fillWithData(values);
            requestData.sendEventWithResponse();

            return requestData;
        } finally {
            inStream = null;
            outStream = null;
        }
    }
}