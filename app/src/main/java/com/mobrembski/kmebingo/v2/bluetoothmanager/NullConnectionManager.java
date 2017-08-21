package com.mobrembski.kmebingo.v2.bluetoothmanager;

import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataIdent;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEFrame;

public class NullConnectionManager implements ISerialConnectionManager {

    @Override
    public <T extends KMEFrame> void postNewRequest(T requestData, int priority) {
        if (requestData instanceof KMEDataIdent) {
            requestData.fillWithData(new int[] {0x65, 0x4B, 0x42, 0x00});
        }
        if (requestData instanceof KMEDataActual) {
            requestData.fillWithData(new int[] {0x65, 0x18, 0x14, 0x80, 0x80, 0x80, 0x45, 0x80, 0x80, 0x80, 0x7d});
        }
        if (requestData instanceof KMEDataSettings) {
            requestData.fillWithData(new int[] {0x65, 0x6A, 0x00, 0x17, 0x01, 0x88, 0x0B, 0x0B, 0x32, 0x29, 0x11, 0xA1, 0x01, 0x00});
        }
        if (requestData instanceof KMEDataConfig) {
            requestData.fillWithData(new int[] {0x65, 0x32, 0x32, 0xC8, 0x1E, 0x10, 0x27, 0x07, 0x10, 0x27, 0x24, 0xC4, 0x09, 0x37, 0x2D, 0x00});
        }
        if (requestData instanceof KMEDataInfo) {
            requestData.fillWithData(new int[] {0x65, 0x28, 0x5F, 0x89, 0xCD, 0x40, 0xA9, 0x64, 0x29, 0x83, 0x05, 0xD9, 0x06, 0x57, 0x41, 0x31, 0x32, 0x33, 0x34, 0x35, 0x00});
        }
        spawnThreadWithAnswer(requestData);
    }

    private <T extends KMEFrame> void spawnThreadWithAnswer(final T requestData) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
                    requestData.sendEventWithResponse();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    @Override
    public <T extends KMEFrame> T runRequestNow(T requestData) {
        return null;
    }

    @Override
    public SerialConnectionStatusEvent.SerialConnectionStatus getConnectionStatus() {
        return SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED;
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void startConnecting() {

    }

    @Override
    public void stopConnections() {

    }

    @Override
    public int getTransmittedPacketCount() {
        return 753;
    }

    @Override
    public int getErrorsCount() {
        return 1;
    }

    @Override
    public long getConnectedTime() {
        return 0;
    }
}
