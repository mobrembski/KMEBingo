package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;
import com.mobrembski.kmeviewer.SerialFrames.SettingsFrame;

public class KMESettingsTab extends KMEViewerTab {
    public KMESettingsTab() {
        this.layoutId = R.layout.settingstab;
        final AskFrameClass askFrame = new AskFrameClass(new SettingsFrame(), this);
        super.setAskFrame(askFrame);

    }

    @Override
    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KMEDataSettings ds = KMEDataSettings.GetDataFromByteArray(frame);
                    Spinner spinner = (Spinner)getActivity().findViewById(R.id.spinner);
                    TextView tv = (TextView)spinner.getSelectedView();
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.GRAY);
                    spinner.setBackgroundColor(Color.GRAY);
                }
            });
    }
}
