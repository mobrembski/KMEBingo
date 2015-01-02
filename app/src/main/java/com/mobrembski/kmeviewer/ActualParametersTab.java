package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.ActualFrame;
import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;

public class ActualParametersTab extends KMEViewerTab {
    public ActualParametersTab() {
        this.layoutId = R.layout.actualparamtab;
        final AskFrameClass askFrame = new AskFrameClass(new ActualFrame(), this);
        super.setAskFrame(askFrame);
    }

    @Override
    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) myView.findViewById(R.id.textView2);
                    KMEDataActual dtn = KMEDataActual.GetDataFromByteArray(frame);
                    tv.setText(String.valueOf(dtn.TPS));
                }
            });
    }
}