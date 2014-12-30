package com.mobrembski.kmeviewer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observer;

public abstract class KMEViewerTab extends Fragment implements Observer {
    protected int layoutId;
    protected BluetoothController btcntrl = null;
    protected View myView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(layoutId, container, false);
        this.myView = rootView;
        return rootView;
    }

    public void setController(BluetoothController controller) {
        this.btcntrl = controller;
    }
}
