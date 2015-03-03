package com.mobrembski.kmeviewer;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;

import com.mobrembski.kmeviewer.Tabs.KMEViewerTab;

public class TabListener implements ActionBar.TabListener {
    private final KMEViewerTab fragment;

    public TabListener(KMEViewerTab fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(android.R.id.content, fragment);
        BluetoothController.getInstance().AddOnConnectionListener(fragment);
        fragment.onTabSelected();
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        BluetoothController.getInstance().RemoveOnConnectionListener(fragment);
        fragment.onTabUnselected();
        ft.remove(fragment);
    }

    // Nothing special here. Fragments already did the job.
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}
