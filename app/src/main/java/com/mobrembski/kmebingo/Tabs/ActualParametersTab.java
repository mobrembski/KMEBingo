package com.mobrembski.kmebingo.Tabs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobrembski.kmebingo.ActuatorView;
import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.BluetoothController;
import com.mobrembski.kmebingo.ControllerEvent;
import com.mobrembski.kmebingo.GraphRow;
import com.mobrembski.kmebingo.LambdaView;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.RPMView;
import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.TPSView;

import java.util.concurrent.TimeUnit;

public class ActualParametersTab extends KMEViewerTab implements ControllerEvent {
    private final Time TimeOnBenzinStart = new Time();
    private final Time TimeOnBenzinEnd = new Time();
    private GraphRow TPSRow;
    private GraphRow LambdaRow;
    private GraphRow ActuatorRow;
    private GraphRow TemperatureRow;
    private GraphRow RPMRow;
    private TextView IgnitionTV;
    private TextView FuelTypeTV;
    private TextView TempTV;
    private TextView RPMStatusTV;
    private TextView CutOFFTV;
    private TextView TimeSpendOnBenzin;
    private boolean TimeOnBenzinChecked = false;
    private TPSView TpsView;
    private LambdaView lambdaView;
    private RPMView rpmView;
    private ActuatorView actuatorView;
    private KMEDataSettings actualSettings;
    private int LambdaGreenColor;
    private int LambdaYellowColor;
    private int LambdaRedColor;
    private float TpsMaxValue = 5.0f;
    private KMEDataConfig actualConfig;

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
        RPMRow = (GraphRow) myView.findViewById(R.id.RPMChartRow);
        TPSRow = (GraphRow) myView.findViewById(R.id.TPSChartRow);
        LambdaRow = (GraphRow) myView.findViewById(R.id.LambdaChartRow);
        ActuatorRow = (GraphRow) myView.findViewById(R.id.ActuatorChartRow);
        TemperatureRow = (GraphRow) myView.findViewById(R.id.TempChartRow);
        IgnitionTV = (TextView) myView.findViewById(R.id.IgnitionStatusValue);
        FuelTypeTV = (TextView) myView.findViewById(R.id.FuelTypeStatusValue);
        TempTV = (TextView) myView.findViewById(R.id.TempStatusValue);
        RPMStatusTV = (TextView) myView.findViewById(R.id.RPMStatusValue);
        CutOFFTV = (TextView) myView.findViewById(R.id.CutOFFValue);
        LayoutInflater ownInflater = (LayoutInflater) getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup parent = ActuatorRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_actuator_visible, parent);
        actuatorView = (ActuatorView) myView.findViewById(R.id.ActuatorView);
        parent = TemperatureRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.actual_param_tab_temp_hidden, parent);
        TimeSpendOnBenzin = (TextView) myView.findViewById(R.id.ActualParamSpendOnBenzinValue);
        // TODO: Make this values depends from Settings.
        ViewGroup TPSVisible = TPSRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_tps_visible, TPSVisible);
        TpsView = (TPSView) myView.findViewById(R.id.TPSView);
        ViewGroup LambdaVisible = LambdaRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_lambda_visible, LambdaVisible);
        lambdaView = (LambdaView) myView.findViewById(R.id.LambdaView);
        ViewGroup RPMVisible = RPMRow.getInjectVisibleView();
        ownInflater.inflate(R.layout.actual_param_tab_rpm_visible, RPMVisible);
        rpmView = (RPMView) myView.findViewById(R.id.RPMView);
        RPMRow.CreateRenderer(7000, 0, 300, 0);
        TPSRow.CreateRenderer(5, 300);
        LambdaRow.CreateRenderer(1, -1, 300, 0);
        ActuatorRow.CreateRenderer(255, 300);
        TemperatureRow.CreateRenderer(110, 0, 5000, 0);
        getActualConfigAndPrepareViews();
        return v;
    }

    private int getTpsFillColor(int rawVal) {
        switch (rawVal) {
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
        switch (rawVal) {
            case 3:
            case 1:
                return LambdaGreenColor;
            case 6:
            case 4:
                return LambdaRedColor;
            default:
                return LambdaYellowColor;
        }
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null) {
            final KMEDataActual dtn = new KMEDataActual(frame);
            final int TPSFillColor = getTpsFillColor(dtn.TPSColor);
            final int LambdaColor = getLambdaColor(dtn.LambdaColor);
            final float TPSPercentage = (dtn.TPS / TpsMaxValue) * 100;
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RPMRow.SetValueText(String.valueOf(dtn.RPM));
                        RPMRow.AddPoint(dtn.RPM);
                        rpmView.setRpmValue(dtn.RPM);
                        TPSRow.SetValueText(String.valueOf(dtn.TPS) + " V");
                        TPSRow.AddPoint(dtn.TPS);
                        TPSRow.SetAdditionalValueText(String.format("%.1f%%", TPSPercentage));
                        TpsView.setRectFilled(TPSFillColor);
                        LambdaRow.SetValueText(String.valueOf(dtn.Lambda) + " V");
                        LambdaRow.AddPoint(dtn.Lambda);
                        lambdaView.setLambdaValue(dtn.Lambda, LambdaColor);
                        ActuatorRow.SetValueText(String.valueOf(dtn.Actuator));
                        ActuatorRow.AddPoint(dtn.Actuator);
                        ActuatorRow.SetAdditionalValueText("PWA: "+String.valueOf(dtn.PWA));
                        actuatorView.setDataConfigFrame(actualConfig);
                        actuatorView.setPWAValue(dtn.PWA);
                        actuatorView.setActuatorSteps(dtn.Actuator);
                        TemperatureRow.SetValueText(String.valueOf(dtn.ActualTemp) + " °C");
                        TemperatureRow.SetAdditionalValueText("ON: " +
                                String.valueOf(BitUtils.GetTemperature(
                                        actualSettings.getLPGOnTemperature())) + " °C");
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
                    } catch (NullPointerException ex) {
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
        getActualConfigAndPrepareViews();
        super.onConnectionStarting();
        TimeOnBenzinStart.setToNow();
    }

    private void getActualConfigAndPrepareViews() {
        if (!BluetoothController.getInstance().IsConnected())
            return;
        actualSettings = new KMEDataSettings(BluetoothController.getInstance()
                .askForFrame(new KMEDataSettings()));
        actualConfig = new KMEDataConfig(BluetoothController.getInstance()
                .askForFrame(new KMEDataConfig()));
        if (actualSettings != null) {
            Activity main = getActivity();
            if (main != null)
                main.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 5 Volts TPS
                        if (actualSettings.getTPSType() < 2) {
                            TpsMaxValue = 5.0f;
                            TPSRow.CreateRenderer(5, 300);
                        }
                        else {
                            TpsMaxValue = 12.0f;
                            TPSRow.CreateRenderer(12, 300);
                        }
                        // 0-1 Volts lambda
                        if (actualSettings.getLambdaType() == 0 ||
                                actualSettings.getLambdaType() == 5) {
                            lambdaView.setLambdaMax(1.0f);
                            LambdaRow.CreateRenderer(1, 300);
                        }
                        else {
                            lambdaView.setLambdaMax(5.0f);
                            LambdaRow.CreateRenderer(5, 300);
                        }
                    }
                });
        }
    }
}