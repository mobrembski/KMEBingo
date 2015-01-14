package com.mobrembski.kmeviewer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

public abstract class KMEViewerTab extends Fragment implements ControllerEvent {
    protected int layoutId;
    protected View myView = null;
    protected Thread askingThread = null;
    protected boolean askingThreadRunning = false;
    protected int askingDelay = 100;

    protected KMEFrame askFrame = null;

    private Runnable askingRunnable = new Runnable() {
        @Override
        public void run() {
            while (askingThreadRunning) {
                packetReceived(BluetoothController.getInstance().askForFrame(askFrame));
                try {
                    Thread.sleep(askingDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    };

    protected abstract void packetReceived(int[] ints);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutId, container, false);
        this.myView = rootView;
        return rootView;
    }

    protected void setAskFrame(KMEFrame askFrame) {
        this.askFrame = askFrame;
    }

    @Override
    public void onConnectionStopping() {
        askingThreadRunning = false;
        if (askingThread != null) {
            try {
                askingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        askingThread = null;
    }

    @Override
    public void onConnectionStarting() {
        // TabListener already invokes this method, so return...
        if (askingThread != null)
            return;
        // If Tab is "configuration" tab, then we don't want to
        // refresh configuration automatically
        if (askFrame == null)
            return;
        askingThread = new Thread(askingRunnable);
        askingThreadRunning = true;
        askingThread.start();
    }
}
