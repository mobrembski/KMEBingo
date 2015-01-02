package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.graphics.Color;
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
                    KMEDataActual dtn = KMEDataActual.GetDataFromByteArray(frame);
                    TextView tv = (TextView) myView.findViewById(R.id.TPSValue);
                    tv.setText(String.valueOf(dtn.TPS));
                    tv = (TextView) myView.findViewById(R.id.LambdaValue);
                    tv.setText(String.valueOf(dtn.Lambda));
                    if (dtn.LambdaColor==1) {
                        tv.setTextColor(Color.rgb(0,255,0));
                    } else if (dtn.LambdaColor==4) {
                        tv.setTextColor(Color.rgb(255,0,0));
                    } else {
                        tv.setTextColor(Color.rgb(255,0,255));
                    }
                    tv = (TextView) myView.findViewById(R.id.RPMValue);
                    tv.setText(String.valueOf(dtn.RPMRaw1)+" "+String.valueOf(dtn.RPMRaw2));
                    tv = (TextView) myView.findViewById(R.id.ActuatorValue);
                    tv.setText(String.valueOf(dtn.Actuator)+" PWA:"+String.valueOf(dtn.PWA));
                    tv = (TextView) myView.findViewById(R.id.ActuatorValue);
                    tv.setText(String.valueOf(dtn.Actuator)+" PWA:"+String.valueOf(dtn.PWA));
                    tv = (TextView) myView.findViewById(R.id.TemperatureValue);
                    tv.setText(String.valueOf(dtn.ActualTemp));
                    tv = (TextView) myView.findViewById(R.id.RPMOKValue);
                    if(dtn.RPOK) {
                        tv.setText("RPM OK!");
                        tv.setTextColor(Color.rgb(0,255,0));
                    }
                    else {
                        tv.setText("RPM TOO LOW!");
                        tv.setTextColor(Color.rgb(255,0,0));
                    }
                    tv = (TextView) myView.findViewById(R.id.TemperatureOKValue);
                    if(dtn.TemperatureOK) {
                        tv.setText("TEMP OK!");
                        tv.setTextColor(Color.rgb(0,255,0));
                    }
                    else {
                        tv.setText("TEMP TOO LOW!");
                        tv.setTextColor(Color.rgb(255,0,0));
                    }
                    tv = (TextView) myView.findViewById(R.id.RunningFuelTypeValue);
                    if(dtn.WorkingOnGas) {
                        tv.setText("Working on GAS!");
                        tv.setTextColor(Color.rgb(0,255,0));
                    }
                    else {
                        tv.setText("Working on Benzin!");
                        tv.setTextColor(Color.rgb(255,0,0));
                    }
                    tv = (TextView) myView.findViewById(R.id.CutOffActiveValue);
                    if(dtn.CutOffActivated) {
                        tv.setText("CUT OFF ACTIVE!");
                        tv.setTextColor(Color.rgb(255,255,0));
                    }
                    else {
                        tv.setText("");
                        tv.setTextColor(Color.rgb(255,0,0));
                    }
                    tv = (TextView) myView.findViewById(R.id.IgnitionValue);
                    if(dtn.Ignition) {
                        tv.setText("Ignition ON!");
                        tv.setTextColor(Color.rgb(255,255,0));
                    }
                    else {
                        tv.setText("Ignition OFF");
                        tv.setTextColor(Color.rgb(255,0,0));
                    }
                }
            });
    }
}