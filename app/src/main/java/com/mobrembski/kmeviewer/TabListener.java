package com.mobrembski.kmeviewer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

public class TabListener implements ActionBar.TabListener {
    private KMEViewerTab fragment;
    private BluetoothController controller = null;

    public TabListener(KMEViewerTab fragment, BluetoothController controller) {
        this.fragment = fragment;
        this.controller = controller;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.activity_main, fragment);
        if (this.controller != null) {
            fragment.setController(this.controller);
        }
        this.fragment.askingThreadRunning = true;
        fragment.CreateAskingThread(50);
        fragment.askingThread.start();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        try {
            this.fragment.askingThreadRunning = false;
            if (this.fragment.askingThread != null)
                this.fragment.askingThread.join();
            // TODO: Seems to be a workaround for fixing a lag
            // on switching tab...To be done in future.
            this.controller.queue.clear();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ft.remove(fragment);
    }

    // Nothing special here. Fragments already did the job.
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}
