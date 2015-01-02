package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;
import com.mobrembski.kmeviewer.SerialFrames.OtherSettingsFrame;

public class KmeInfoTab extends KMEViewerTab {
    public KmeInfoTab() {
        this.layoutId = R.layout.kmeinfotab;
        final AskFrameClass askFrame = new AskFrameClass(new OtherSettingsFrame(), this);
        super.setAskFrame(askFrame);
    }

    @Override
    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KMEDataInfo dtn = KMEDataInfo.GetDataFromByteArray(frame);
                    TextView tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
                    tv.setText(String.valueOf(dtn.hoursOnGas+"h "+dtn.minutesOnGas+"min"));
                    tv = (TextView) myView.findViewById(R.id.RegistrationPlateValue);
                    tv.setText(dtn.RegistrationPlate);
                }
            });
    }
}
