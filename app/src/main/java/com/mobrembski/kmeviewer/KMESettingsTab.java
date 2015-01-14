package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.mobrembski.kmeviewer.SerialFrames.SettingsFrame;

import java.util.ArrayList;
import java.util.List;

public class KMESettingsTab extends KMEViewerTab implements ControllerEvent {
    private Spinner LambdaNeutralPointSpinner;
    private Spinner LambdaTypeSpinner;
    private Spinner LambdaDelaySpinner;
    private Spinner LambdaEmulationHTimeSpinner;
    private Spinner LambdaEmulationLTimeSpinner;
    private Spinner IgnitionTypeSpinner;
    private Spinner GasONRPMSpinner;
    private Spinner SwitchOnSpinner;
    private Spinner TPSTypeSpinner;
    private Spinner TPSInertial;
    private CheckBox EngGasOnCheckbox;
    private Spinner EngGasStartSpinner;

    public KMESettingsTab() {
        this.layoutId = R.layout.settingstab;
        super.setAskFrame(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LambdaTypeSpinner = (Spinner) myView.findViewById(R.id.LambdaTypeSpinner);
        LambdaNeutralPointSpinner = (Spinner) myView.findViewById(R.id.LambdaNeutralPointSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                myView.getContext(),
                R.array.lambda_neutral_point_low,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LambdaNeutralPointSpinner.setAdapter(adapter);

        LambdaDelaySpinner = (Spinner) myView.findViewById(R.id.LambdaDelaySpinner);
        List<String> stringi = new ArrayList<String>();
        for (int i = 0; i < 255; i++)
            stringi.add(String.valueOf(i));
        ArrayAdapter<String> adapterDelayTime = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, stringi);
        adapterDelayTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LambdaDelaySpinner.setAdapter(adapterDelayTime);

        LambdaEmulationHTimeSpinner = (Spinner) myView.findViewById(R.id.LambdaEmulationHTimeSpinner);
        List<String> HTimeSpinnerStringlist = new ArrayList<String>();
        for (float f = 0.025f; f < 6.375; f += 0.025)
            HTimeSpinnerStringlist.add(String.valueOf(f));
        ArrayAdapter<String> adapterHTime = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, HTimeSpinnerStringlist);
        adapterDelayTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LambdaEmulationHTimeSpinner.setAdapter(adapterHTime);

        LambdaEmulationLTimeSpinner = (Spinner) myView.findViewById(R.id.LambdaEmulationLTimeSpinner);
        ArrayAdapter<String> adapterLTime = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, HTimeSpinnerStringlist);
        adapterDelayTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LambdaEmulationLTimeSpinner.setAdapter(adapterLTime);

        IgnitionTypeSpinner = (Spinner) myView.findViewById(R.id.IgnitionTypeSpinner);
        ArrayAdapter<CharSequence> ignitionTypeAdapter = ArrayAdapter.createFromResource(
                myView.getContext(),
                R.array.ignition_type,
                android.R.layout.simple_spinner_item);
        ignitionTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        IgnitionTypeSpinner.setAdapter(ignitionTypeAdapter);

        GasONRPMSpinner = (Spinner) myView.findViewById(R.id.GasONRPMSpinner);
        List<String> GasOnRPMStringlist = new ArrayList<String>();
        for (int i = 1500; i < 3500; i += 100)
            GasOnRPMStringlist.add(String.valueOf(i));
        ArrayAdapter<String> adapterGasOnRPM = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, GasOnRPMStringlist);
        adapterGasOnRPM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        GasONRPMSpinner.setAdapter(adapterGasOnRPM);

        SwitchOnSpinner = (Spinner) myView.findViewById(R.id.SwitchOnSpinner);
        ArrayAdapter<CharSequence> switchOnAdapter = ArrayAdapter.createFromResource(
                myView.getContext(),
                R.array.switch_on_routes,
                android.R.layout.simple_spinner_item);
        switchOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SwitchOnSpinner.setAdapter(switchOnAdapter);

        TPSTypeSpinner = (Spinner) myView.findViewById(R.id.TPSTypeSpinner);
        ArrayAdapter<CharSequence> TPStypeAdapter = ArrayAdapter.createFromResource(
                myView.getContext(),
                R.array.TPS_type,
                android.R.layout.simple_spinner_item);
        TPStypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TPSTypeSpinner.setAdapter(TPStypeAdapter);

        TPSInertial = (Spinner) myView.findViewById(R.id.TPSInertial);
        List<String> TPSInertialSpinnerStringlist = new ArrayList<String>();
        for (float f = 0.04f; f < 0.4; f += 0.02)
            TPSInertialSpinnerStringlist.add(String.valueOf(f));
        ArrayAdapter<String> TPSInertialAdapter = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, TPSInertialSpinnerStringlist);
        TPSInertialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        TPSInertial.setAdapter(TPSInertialAdapter);

        EngGasOnCheckbox = (CheckBox) myView.findViewById(R.id.EngGasStartCheckbox);
        EngGasStartSpinner = (Spinner) myView.findViewById(R.id.EngGasStartSpinner);
        List<String> EngGasStartSpinnerStringlist = new ArrayList<String>();
        for (float f = 0.1f; f < 5.0f; f += 0.1)
            EngGasStartSpinnerStringlist.add(String.valueOf(f));
        ArrayAdapter<String> EngGasStartlAdapter = new ArrayAdapter<String>(
                myView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, EngGasStartSpinnerStringlist);
        EngGasStartlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        EngGasStartSpinner.setAdapter(EngGasStartlAdapter);

        packetReceived(BluetoothController.getInstance().askForFrame(new SettingsFrame()));
        return v;
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KMEDataSettings ds = KMEDataSettings.GetDataFromByteArray(frame);
                    LambdaTypeSpinner.setSelection(ds.LambdaType);
                    //LambdaNeutralPointSpinner.setSelection(ds.LambdaNeutralPoint-1);
                    LambdaDelaySpinner.setSelection(ds.LambdaDelay);
                    LambdaEmulationHTimeSpinner.setSelection(ds.LambdaEmulationHState - 1);
                    LambdaEmulationLTimeSpinner.setSelection(ds.LambdaEmulationLState - 1);
                    if (ds.TurnOnAtIncreasingRPM)
                        SwitchOnSpinner.setSelection(0);
                    else
                        SwitchOnSpinner.setSelection(1);
                    TPSTypeSpinner.setSelection(ds.TPSType);
                    TPSInertial.setSelection(ds.TPSInertial);
                    EngGasStartSpinner.setSelection(ds.StartOnGasOpenTime - 1);
                    if (ds.StartOnGas)
                        EngGasOnCheckbox.setChecked(true);
                    else
                        EngGasOnCheckbox.setChecked(false);
                }
            });
    }
}
