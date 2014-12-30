package com.mobrembski.kmeviewer;

import android.widget.TextView;

import java.util.Observable;

public class ActualParametersTab extends KMEViewerTab {
    public ActualParametersTab() {
        this.layoutId = R.layout.actualparamtab;
        if(this.btcntrl!=null) {
            this.btcntrl.addObserver(this);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView)myView.findViewById(R.id.textView2);
                KMEDataActual dtn = btcntrl.GetActualParameters();
                tv.setText(String.valueOf(dtn.TPS));
            }
        });

    }

    @Override
    public void onDestroy() {
        if(this.btcntrl!=null) {
            btcntrl.deleteObserver(this);
        }
        super.onDestroy();
    }
}