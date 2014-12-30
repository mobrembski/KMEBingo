package com.mobrembski.kmeviewer;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.app.ActionBar;

public class TabListener implements ActionBar.TabListener {

    private KMEViewerTab fragment;
    private BluetoothController controller = null;

    // The contructor.
    public TabListener(KMEViewerTab fragment, BluetoothController controller) {
        this.fragment = fragment;
        this.controller = controller;
    }

    // When a tab is tapped, the FragmentTransaction replaces
    // the content of our main layout with the specified fragment;
    // that's why we declared an id for the main layout.
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.activity_main, fragment);
        if(this.controller!=null)
            fragment.setController(this.controller);
    }

    // When a tab is unselected, we have to hide it from the user's view.
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        if(this.controller!=null)
            this.controller.deleteObserver(fragment);
            ft.remove(fragment);
    }

    // Nothing special here. Fragments already did the job.
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {

    }
}
