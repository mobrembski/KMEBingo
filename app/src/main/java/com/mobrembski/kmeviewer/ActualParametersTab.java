package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.widget.TextView;

import java.util.Observable;

public class ActualParametersTab extends KMEViewerTab {
    public ActualParametersTab() {
        this.layoutId = R.layout.actualparamtab;
    }

    @Override
    public void update(Observable observable, Object o) {
        Activity main = getActivity();
        if(main!=null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView)myView.findViewById(R.id.textView2);
                    KMEDataActual dtn = btcntrl.GetActualParameters();
                    tv.setText(String.valueOf(dtn.TPS));
                }
            });

    }
}