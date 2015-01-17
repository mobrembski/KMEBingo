package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.ActualFrame;

public class ActualParametersTab extends KMEViewerTab implements ControllerEvent {
    private int ValueOKColor;
    private int ValueNotOKColor;
    private int ValueNotImportantColor;
    private int LambdaGreenColor;
    private int LambdaYellowColor;
    private int LambdaRedColor;

    public ActualParametersTab() {
        this.layoutId = R.layout.actualparamtab;
        this.askFrame = new ActualFrame();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ValueOKColor = getResources().getColor(R.color.ValueOK);
        ValueNotOKColor = getResources().getColor(R.color.ValueNotOK);
        ValueNotImportantColor = getResources().getColor(R.color.ValueNotImportant);
        LambdaGreenColor = getResources().getColor(R.color.LambdaGreen);
        LambdaYellowColor = getResources().getColor(R.color.LambdaYellow);
        LambdaRedColor = getResources().getColor(R.color.LambdaRed);
        return v;
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KMEDataActual dtn = KMEDataActual.GetDataFromByteArray(frame);
                    TextView tv = (TextView) myView.findViewById(R.id.TPSValue);
                    tv.setText(String.valueOf(dtn.TPS)+" V");
                    tv = (TextView) myView.findViewById(R.id.LambdaValue);
                    tv.setText(String.valueOf(dtn.Lambda)+" V");
                    if (dtn.LambdaColor == 1) {
                        tv.setTextColor(LambdaGreenColor);
                    } else if (dtn.LambdaColor == 4) {
                        tv.setTextColor(LambdaRedColor);
                    } else {
                        tv.setTextColor(LambdaYellowColor);
                    }
                    tv = (TextView) myView.findViewById(R.id.RPMValue);
                    tv.setText(String.valueOf(dtn.RPMRaw1) + " " + String.valueOf(dtn.RPMRaw2));
                    tv = (TextView) myView.findViewById(R.id.ActuatorValue);
                    tv.setText(String.valueOf(dtn.Actuator) + " PWA:" + String.valueOf(dtn.PWA));
                    tv = (TextView) myView.findViewById(R.id.ActuatorValue);
                    tv.setText(String.valueOf(dtn.Actuator) + " PWA:" + String.valueOf(dtn.PWA));
                    tv = (TextView) myView.findViewById(R.id.TemperatureValue);
                    tv.setText(String.valueOf(dtn.ActualTemp)+" °C");
                    tv = (TextView) myView.findViewById(R.id.RPMOKValue);
                    if (dtn.RPOK) {
                        tv.setText("RPM OK!");
                        tv.setTextColor(ValueOKColor);
                    } else {
                        tv.setText("RPM TOO LOW!");
                        tv.setTextColor(ValueNotOKColor);
                    }
                    tv = (TextView) myView.findViewById(R.id.TemperatureOKValue);
                    if (dtn.TemperatureOK) {
                        tv.setText("TEMP OK!");
                        tv.setTextColor(ValueOKColor);
                    } else {
                        tv.setText("TEMP TOO LOW!");
                        tv.setTextColor(ValueNotOKColor);
                    }
                    tv = (TextView) myView.findViewById(R.id.RunningFuelTypeValue);
                    if (dtn.WorkingOnGas) {
                        tv.setText("Working on GAS!");
                        tv.setTextColor(ValueOKColor);
                    } else {
                        tv.setText("Working on Benzin!");
                        tv.setTextColor(ValueNotImportantColor);
                    }
                    tv = (TextView) myView.findViewById(R.id.CutOffActiveValue);
                    if (dtn.CutOffActivated) {
                        tv.setText("CUT OFF ACTIVE!");
                    } else {
                        tv.setText("");
                    }
                    tv = (TextView) myView.findViewById(R.id.IgnitionValue);
                    if (dtn.Ignition) {
                        tv.setText("Ignition ON!");
                        tv.setTextColor(ValueOKColor);
                    } else {
                        tv.setText("Ignition OFF");
                        tv.setTextColor(ValueNotImportantColor);
                    }

                    tv = (TextView) myView.findViewById(R.id.RPMTooHighValue);
                    if (dtn.RPMTooHigh) {
                        tv.setText("RPM too high!");
                        tv.setTextColor(ValueNotOKColor);
                    } else {
                        tv.setText("RPM OK");
                        tv.setTextColor(ValueOKColor);
                    }
                }
            });
    }
}