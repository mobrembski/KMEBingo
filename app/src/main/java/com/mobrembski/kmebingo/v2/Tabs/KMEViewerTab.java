package com.mobrembski.kmebingo.v2.Tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mobrembski.kmebingo.v2.bluetoothmanager.ISerialConnectionManager;

import org.greenrobot.eventbus.EventBus;

public abstract class KMEViewerTab extends android.support.v4.app.Fragment {
    protected int layoutId;
    protected View myView = null;
    private View noConnectOverlay;
    protected ISerialConnectionManager btManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout fl = new FrameLayout(getActivity());
        FrameLayout.LayoutParams layoutParamsFrame = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
        fl.setLayoutParams(layoutParamsFrame);

        View rootView = inflater.inflate(layoutId, container, false);
        this.myView = rootView;
        fl.addView(rootView);
//        noConnectOverlay = inflater.inflate(R.layout.noconnect_overlay, (ViewGroup) myView.getParent());
//
//        fl.addView(noConnectOverlay);
//
//        MainActivity act = (MainActivity) getActivity();
//        this.btManager = act.btManager;
//        // TODO fix this strange bug
//        boolean isSocketConnected = !btManager.isConnected();
//        if(isSocketConnected) {
//            noConnectOverlay.bringToFront();
//        }
        return fl;
    }

    public void onTabSelected() {

    }

    public void onTabUnselected() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            EventBus.getDefault().register(this);
            sendInitialRequestsToDevice();
        } else {
            super.onPause();
            try {
                EventBus.getDefault().unregister(this);
            } catch (Exception e) {
                Log.e("KMEViewerTab", "Exception during unregistering event", e);
            }
        }
    }

    public void setBtManager(ISerialConnectionManager manager) {
        btManager = manager;
        sendInitialRequestsToDevice();
    }

    protected void sendInitialRequestsToDevice() {
        if(btManager == null) return;
    }
}
