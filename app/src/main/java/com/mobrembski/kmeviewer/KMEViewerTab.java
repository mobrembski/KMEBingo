package com.mobrembski.kmeviewer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;

public abstract class KMEViewerTab extends Fragment implements PacketReceivedWaiter {
    public Thread askingThread = null;
    public boolean askingThreadRunning = true;
    protected int layoutId;
    protected BluetoothController btcntrl = null;
    protected View myView = null;
    AskFrameClass askFrame = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutId, container, false);
        this.myView = rootView;
        return rootView;
    }

    public void CreateAskingThread(final int milisecInterval) {
        this.askingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (askingThreadRunning) {
                    try {
                        btcntrl.queue.add(askFrame);
                        Thread.sleep(milisecInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void setController(BluetoothController controller) {
        BluetoothController cc = controller;
        this.btcntrl = controller;
    }

    protected void setAskFrame(AskFrameClass askFrame) {
        this.askFrame = askFrame;
    }
}
