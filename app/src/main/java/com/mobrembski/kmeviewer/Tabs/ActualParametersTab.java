package com.mobrembski.kmeviewer.Tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobrembski.kmeviewer.ControllerEvent;
import com.mobrembski.kmeviewer.GraphRow;
import com.mobrembski.kmeviewer.LambdaView;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataActual;
import com.mobrembski.kmeviewer.TPSView;

import java.util.concurrent.TimeUnit;

public class ActualParametersTab extends KMEViewerTab implements ControllerEvent {
    private int LambdaGreenColor;
    private int LambdaYellowColor;
    private int LambdaRedColor;
    GraphRow TPSRow,LambdaRow,ActuatorRow,TemperatureRow;
    TextView IgnitionTV, FuelTypeTV, TempTV, RPMStatusTV, CutOFFTV, TimeSpendOnBenzin;
    Time TimeOnBenzinStart = new Time();
    Time TimeOnBenzinEnd = new Time();
    boolean TimeOnBenzinChecked = false;
    TPSView TpsView;
    LambdaView lambdaView;

    public ActualParametersTab() {
        this.layoutId = R.layout.actual_param_tab;
        this.askFrame = new KMEDataActual();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LambdaGreenColor = getResources().getColor(R.color.LambdaGreen);
        LambdaYellowColor = getResources().getColor(R.color.LambdaYellow);
        LambdaRedColor = getResources().getColor(R.color.LambdaRed);
        TPSRow = (GraphRow)myView.findViewById(R.id.TPSChartRow);
        LambdaRow = (GraphRow)myView.findViewById(R.id.LambdaChartRow);
        ActuatorRow = (GraphRow)myView.findViewById(R.id.ActuatorChartRow);
        TemperatureRow = (GraphRow)myView.findViewById(R.id.TempChartRow);
        IgnitionTV = (TextView)myView.findViewById(R.id.IgnitionStatusValue);
        FuelTypeTV = (TextView)myView.findViewById(R.id.FuelTypeStatusValue);
        TempTV = (TextView)myView.findViewById(R.id.TempStatusValue);
        RPMStatusTV = (TextView)myView.findViewById(R.id.RPMStatusValue);
        CutOFFTV = (TextView)myView.findViewById(R.id.CutOFFValue);
        LayoutInflater ownInflater = (LayoutInflater) getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = ActuatorRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.actual_param_tab_actuator_hidden, parent);
        parent = TemperatureRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.actual_param_tab_temp_hidden, parent);
        TimeSpendOnBenzin = (TextView)myView.findViewById(R.id.ActualParamSpendOnBenzinValue);
        // TODO: Make this values depends from Settings.
        ViewGroup TPSVisible = TPSRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_tps_visible, TPSVisible);
        TpsView = (TPSView)myView.findViewById(R.id.TPSView);
        ViewGroup LambdaVisible = LambdaRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_lambda_visible, LambdaVisible);
        lambdaView = (LambdaView)myView.findViewById(R.id.LambdaView);
        TPSRow.CreateRenderer(5,300);
        LambdaRow.CreateRenderer(1,-1,300,0);
        ActuatorRow.CreateRenderer(150,300);
        TemperatureRow.CreateRenderer(80,0,5000,0);
        return v;
    }

    private int getTpsFillColor(int rawVal) {
        switch(rawVal)
        {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 4:
                return 3;
            case 8:
                return 4;
        }
        return 0;
    }

    private int getLambdaColor(int rawVal) {
        switch (rawVal)
        {
            case 1:
                return LambdaGreenColor;
            case 4:
                return LambdaRedColor;
            default:
                return LambdaYellowColor;
        }
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null) {
            final KMEDataActual dtn = KMEDataActual.GetDataFromByteArray(frame);
            final int TPSFillColor = getTpsFillColor(dtn.TPSColor);
            final int LambdaColor = getLambdaColor(dtn.LambdaColor);
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TPSRow.SetValueText(String.valueOf(dtn.TPS) + " V");
                        TPSRow.AddPoint(dtn.TPS);
                        TpsView.setRectFilled(TPSFillColor);
                        LambdaRow.SetValueText(String.valueOf(dtn.Lambda) + " V");
                        LambdaRow.AddPoint(dtn.Lambda);
                        lambdaView.setLambdaValue(dtn.Lambda, LambdaColor);
                        LambdaRow.SetValueColor(LambdaColor);
                        ActuatorRow.SetValueText(String.valueOf(dtn.Actuator));
                        ActuatorRow.AddPoint(dtn.Actuator);
                        TextView tv = (TextView) myView.findViewById(R.id.ActualParamPWAValue);
                        tv.setText(String.valueOf(dtn.PWA));
                        TemperatureRow.SetValueText(String.valueOf(dtn.ActualTemp) + " °C");
                        TemperatureRow.AddPoint(dtn.ActualTemp);
                        if (dtn.Ignition) {
                            IgnitionTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                            IgnitionTV.setText("Ignition ON");
                            if (dtn.WorkingOnGas) {
                                FuelTypeTV.setText("On LPG");
                                FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                            } else {
                                FuelTypeTV.setText("On Benzin");
                                FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                            }
                            if (dtn.CutOffActivated) {
                                CutOFFTV.setText("Cut-OFF Active!");
                                CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                            } else {
                                CutOFFTV.setText("Cut-OFF Disabled");
                                CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                            }
                            if (dtn.RPMTooHigh) {
                                CutOFFTV.setText("RPM too High");
                                CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                            }
                            if (dtn.TemperatureOK) {
                                TempTV.setText("Temperature OK");
                                TempTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                            } else {
                                TempTV.setText("Temperature LOW");
                                TempTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                            }
                            if (dtn.RPOK) {
                                RPMStatusTV.setText("RPM OK");
                                RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                            } else {
                                RPMStatusTV.setText("RPM Too LOW");
                                RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                            }
                        } else {
                            IgnitionTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                            IgnitionTV.setText("Ignition OFF");
                            RPMStatusTV.setText("RPM Too LOW");
                            RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                            TempTV.setText("Temp Too Low");
                            TempTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                            CutOFFTV.setText("Cut-OFF Disabled");
                            CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                            FuelTypeTV.setText("On Benzin");
                            FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                        }
                        if (!TimeOnBenzinChecked && dtn.WorkingOnGas) {
                            TimeOnBenzinEnd.setToNow();
                            TimeOnBenzinChecked = true;
                        }
                        if (TemperatureRow.GetHiddenVisibility()) {
                            if (!TimeOnBenzinChecked)
                                TimeOnBenzinEnd.setToNow();
                            long diff = TimeUnit.MILLISECONDS.toSeconds(
                                    TimeOnBenzinEnd.toMillis(true) - TimeOnBenzinStart.toMillis(true));
                            if (diff < 0)
                                diff = 0;
                            TimeSpendOnBenzin.setText(DateUtils.formatElapsedTime(diff));
                        }
                    }
                    catch(NullPointerException ex) {
                        // TODO: This is a hack. After rotating screen, elements are NULL.
                        // Beside null-checking of everything, now it just quits this frame.
                        // However i think there must be better solution.
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionStarting() {
        super.onConnectionStarting();
        TimeOnBenzinStart.setToNow();
    }
}