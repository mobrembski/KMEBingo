package com.mobrembski.kmebingo.Tabs;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobrembski.kmebingo.ActuatorView;
import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.ExpandableRowView;
import com.mobrembski.kmebingo.GraphView;
import com.mobrembski.kmebingo.LambdaView;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.RPMView;
import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.TPSView;
import com.mobrembski.kmebingo.activites.MainActivity;
import com.mobrembski.kmebingo.bluetoothmanager.ISerialConnectionManager;

import org.greenrobot.eventbus.Subscribe;

public class ActualParametersTab extends KMEViewerTab {
    private TextView IgnitionTV;
    private TextView FuelTypeTV;
    private TextView TempTV;
    private TextView RPMStatusTV;
    private TextView CutOFFTV;
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
    private ISerialConnectionManager btManager;
    private GraphView TemperatureGraphView;
    private ExpandableRowView RpmRowView;
    private ExpandableRowView TpsRowView;
    private ExpandableRowView LambdaRowView;
    private ExpandableRowView ActuatorRowView;
    private ExpandableRowView TemperatureRowView;
    private GraphView ActuatorGraphView;
    private GraphView LambdaGraphView;
    private GraphView RpmGraphView;
    private GraphView TpsGraphView;

    public ActualParametersTab() {
        this.layoutId = R.layout.actual_param_tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setupViews();
        setupRenderers();
        InitializeViewsWithZero();
        return v;
    }

    private void setupViews()
    {
        LambdaGreenColor = getResources().getColor(R.color.LambdaGreen);
        LambdaYellowColor = getResources().getColor(R.color.LambdaYellow);
        LambdaRedColor = getResources().getColor(R.color.LambdaRed);
        IgnitionTV = (TextView) myView.findViewById(R.id.IgnitionStatusValue);
        FuelTypeTV = (TextView) myView.findViewById(R.id.FuelTypeStatusValue);
        TempTV = (TextView) myView.findViewById(R.id.TempStatusValue);
        RPMStatusTV = (TextView) myView.findViewById(R.id.RPMStatusValue);
        CutOFFTV = (TextView) myView.findViewById(R.id.CutOFFValue);
        rpmView = (RPMView)myView.findViewById(R.id.rpmView);
        TpsView = (TPSView) myView.findViewById(R.id.TPSView);
        lambdaView = (LambdaView)myView.findViewById(R.id.lambdaView);
        actuatorView = (ActuatorView)myView.findViewById(R.id.ActuatorView);
        TemperatureGraphView = (GraphView)myView.findViewById(R.id.GraphViewForTemperature);
        RpmRowView = (ExpandableRowView)myView.findViewById(R.id.RpmRowView);
        TpsRowView = (ExpandableRowView)myView.findViewById(R.id.TpsRowView);
        LambdaRowView = (ExpandableRowView)myView.findViewById(R.id.LambdaRowView);
        ActuatorRowView = (ExpandableRowView)myView.findViewById(R.id.ActuatorRowView);
        RpmGraphView = (GraphView)myView.findViewById(R.id.GraphViewForRPM);
        TpsGraphView = (GraphView)myView.findViewById(R.id.GraphViewForTPS);
        LambdaGraphView = (GraphView)myView.findViewById(R.id.GraphViewForLambda);
        ActuatorGraphView = (GraphView)myView.findViewById(R.id.GraphViewForActuator);
        TemperatureRowView = (ExpandableRowView)myView.findViewById(R.id.TemperatureRowView);
    }

    private void setupRenderers() {
        RpmGraphView.CreateRenderer(7000, 0, 300, 0);
        ActuatorGraphView.CreateRenderer(255, 300);
        TemperatureGraphView.CreateRenderer(110, 0, 300, 0);
    }

    private void InitializeViewsWithZero() {
        packetReceived(new KMEDataSettings());
        packetReceived(new KMEDataActual());
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        this.btManager = mainActivity.btManager;
        sendInitialRequestsToDevice();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            sendInitialRequestsToDevice();
        }
    }

    @Subscribe
    public void onEvent(final KMEDataActual ignored) {
        Log.d("DebugBT", "EventReceived Actual");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                packetReceived(ignored);
            }
        });
        btManager.postNewRequest(new KMEDataActual(), 1);
    }

    @Subscribe
    public void onEvent(KMEDataConfig ignored) {
        Log.d("ActualParamTab", "EventReceived Config");
        actualConfig = ignored;
    }

    @Subscribe
    public void onEvent(KMEDataSettings ignored) {
        Log.d("ActualParamTab", "EventReceived Config");
        actualSettings = ignored;
        packetReceived(actualSettings);
    }

    private void sendInitialRequestsToDevice() {
        if (btManager == null) return;
        btManager.postNewRequest(new KMEDataSettings(), 1);
        btManager.postNewRequest(new KMEDataConfig(), 1);
        btManager.postNewRequest(new KMEDataActual(), 1);
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

    public void packetReceived(final KMEDataActual dtn) {
        Activity main = getActivity();
        if (main != null && dtn != null) {
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        UpdateViewsDependOnRPM();
                        UpdateViewsDependOnTPS();
                        UpdateViewsDependOnLambda();
                        UpdateViewsDependOnActuator();
                        UpdateViewsDependOnTemperature();
                        if (dtn.Ignition) {
                            UpdateViewsForIgnitionTurnedOn();
                        } else {
                            UpdateViewsForIgnitionTurnedOff();
                        }
                    } catch (NullPointerException ex) {
                        // TODO: This is a hack. After rotating screen, elements are NULL.
                        // Beside null-checking of everything, now it just quits this frame.
                        // However i think there must be better solution.
                    }
                }

                private void UpdateViewsForIgnitionTurnedOff() {
                    IgnitionTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                    IgnitionTV.setText(R.string.ignition_off);
                    RPMStatusTV.setText(R.string.rpm_too_low);
                    RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                    TempTV.setText(R.string.temp_too_low);
                    TempTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                    CutOFFTV.setText(R.string.cut_off_disabled);
                    CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                    FuelTypeTV.setText(R.string.on_benzin);
                    FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                }

                private void UpdateViewsForIgnitionTurnedOn() {
                    IgnitionTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                    IgnitionTV.setText(R.string.ignition_on);
                    if (dtn.WorkingOnGas) {
                        FuelTypeTV.setText(R.string.on_lpg);
                        FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                    } else {
                        FuelTypeTV.setText(R.string.on_benzin);
                        FuelTypeTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                    }
                    if (dtn.CutOffActivated) {
                        CutOFFTV.setText(R.string.cut_off_active);
                        CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                    } else {
                        CutOFFTV.setText(R.string.cut_off_disabled);
                        CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextDisabled);
                    }
                    if (dtn.RPMTooHigh) {
                        CutOFFTV.setText(R.string.rpm_too_high);
                        CutOFFTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                    }
                    if (dtn.TemperatureOK) {
                        TempTV.setText(R.string.temp_ok);
                        TempTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                    } else {
                        TempTV.setText(R.string.temp_too_low);
                        TempTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                    }
                    if (dtn.RPOK) {
                        RPMStatusTV.setText(R.string.rpm_ok);
                        RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextNormal);
                    } else {
                        RPMStatusTV.setText(R.string.rpm_too_low);
                        RPMStatusTV.setTextAppearance(getActivity(), R.style.StatusTextCritical);
                    }
                }

                private void UpdateViewsDependOnTemperature() {
                    TemperatureRowView.setAdditionalText(getString(R.string.temp_on) +
                            String.valueOf(BitUtils.GetTemperature(
                                   actualSettings.getLPGOnTemperature())) + " °C");
                    TemperatureRowView.setValueText(String.valueOf(dtn.ActualTemp));
                    TemperatureGraphView.AddPoint(dtn.ActualTemp);
                }

                private void UpdateViewsDependOnActuator() {
                    ActuatorRowView.setAdditionalText("PWA: "+String.valueOf(dtn.PWA));
                    ActuatorRowView.setValueText(String.valueOf(dtn.Actuator));
                    ActuatorGraphView.AddPoint(dtn.Actuator);
                    actuatorView.setDataConfigFrame(actualConfig);
                    actuatorView.setPWAValue(dtn.PWA);
                    actuatorView.setActuatorSteps(dtn.Actuator);
                }

                private void UpdateViewsDependOnLambda() {
                    final int LambdaColor = getLambdaColor(dtn.LambdaColor);
                    LambdaRowView.setValueText(String.valueOf(dtn.Lambda));
                    lambdaView.setLambdaValue(dtn.Lambda, LambdaColor);
                    LambdaGraphView.AddPoint(dtn.Lambda);
                }

                private void UpdateViewsDependOnTPS() {
                    final float TPSPercentage = (dtn.TPS / TpsMaxValue) * 100;
                    final int TPSFillColor = getTpsFillColor(dtn.TPSColor);
                    TpsRowView.setAdditionalText(String.format("%.1f%%", TPSPercentage));
                    TpsRowView.setValueText(String.valueOf(dtn.TPS));
                    TpsView.setRectFilled(TPSFillColor);
                    TpsGraphView.AddPoint(dtn.TPS);
                }

                private void UpdateViewsDependOnRPM() {
                    RpmRowView.setValueText(String.valueOf(dtn.RPM));
                    rpmView.setRpmValue(dtn.RPM);
                    RpmGraphView.AddPoint(dtn.RPM);
                }
            });
        }
    }

    public void packetReceived(final KMEDataSettings kmeDataSettings) {
        actualSettings = kmeDataSettings;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CreateTPSGraphDependOnType(actualSettings);
                CreateLambdGraphDependOnType(actualSettings);
            }
        });
    }

    private void CreateTPSGraphDependOnType(final KMEDataSettings kmeDataSettings) {
        // 5 Volts TPS
        if (kmeDataSettings.getTPSType() < 2) {
            TpsMaxValue = 5.0f;
            TpsGraphView.CreateRenderer(5, 300);
        }
        else {
            TpsMaxValue = 12.0f;
            TpsGraphView.CreateRenderer(12, 300);
        }
    }

    private void CreateLambdGraphDependOnType(final KMEDataSettings kmeDataSettings) {
        // 0-1 Volts lambda
        if (kmeDataSettings.getLambdaType() == 0 ||
                kmeDataSettings.getLambdaType() == 5) {
            lambdaView.setLambdaMax(1.0f);
            LambdaGraphView.CreateRenderer(1, 300);
        }
        else {
            lambdaView.setLambdaMax(5.0f);
            LambdaGraphView.CreateRenderer(5, 300);
        }
    }

}