package com.mobrembski.kmeviewer.Tabs;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mobrembski.kmeviewer.BluetoothController;
import com.mobrembski.kmeviewer.ControllerEvent;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

public abstract class KMEViewerTab extends Fragment implements ControllerEvent {
    protected final int askingDelay = 100;
    protected int layoutId;
    protected View myView = null;
    protected Thread askingThread = null;
    protected boolean askingThreadRunning = false;
    protected KMEFrame askFrame = null;
    private View noConnectOverlay;

    private final Runnable askingRunnable = new Runnable() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fl = new FrameLayout(getActivity());
        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        fl.setLayoutParams(layoutParamsFrame);

        View rootView = inflater.inflate(layoutId, container, false);
        this.myView = rootView;
        noConnectOverlay = inflater.inflate(R.layout.noconnect_overlay, (ViewGroup) myView.getParent());
        fl.addView(rootView);
        fl.addView(noConnectOverlay);
        if(BluetoothController.getInstance().IsConnected())
            rootView.bringToFront();
        return fl;
    }

    @Override
    public void onConnectionStopping() {
        stopAskingThread();
        if (noConnectOverlay != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noConnectOverlay.bringToFront();
                }
            });
    }

    @Override
    public void onConnectionStarting() {
        if (myView != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myView.bringToFront();
                }
            });
        createAskingThread();
    }

    public void onTabSelected() {
        createAskingThread();
    }

    public void onTabUnselected() {
        stopAskingThread();
    }

    protected abstract void packetReceived(int[] ints);

    protected void setAskFrame(KMEFrame askFrame) {
        this.askFrame = askFrame;
    }

    private void stopAskingThread() {
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

    private void createAskingThread() {
        if (askFrame == null)
            return;
        askingThread = new Thread(askingRunnable);
        askingThreadRunning = true;
        askingThread.start();
    }
}
