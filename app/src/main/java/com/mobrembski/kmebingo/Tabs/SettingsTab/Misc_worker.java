package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.BluetoothController;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import org.jraf.android.backport.switchwidget.Switch;

import java.util.ArrayList;
import java.util.List;

class Misc_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface, CompoundButton.OnCheckedChangeListener {
    private final KMESettingsTab parent;
    private final Switch TemperatureSensorEnabledSwitch;
    private final Spinner SwitchOnTempSpinner;
    private final Spinner EconomySpinner;
    private final Switch StartOnGasSwitch;
    private final Spinner ValveOpenSpinner;
    private final Spinner SensorTypeSpinner;
    private final Spinner GasBenzinTimeSpinner;
    private KMEDataSettings actualDS;

    public Misc_worker(KMESettingsTab parent) {
        this.parent = parent;
        TemperatureSensorEnabledSwitch = (Switch) parent.usedView.findViewById(R.id.TemperatureSensorEnabledSwitch);
        TemperatureSensorEnabledSwitch.setOnCheckedChangeListener(this);
        SwitchOnTempSpinner = (Spinner) parent.usedView.findViewById(R.id.SwitchOnTempSpinner);
        SwitchOnTempSpinner.setAdapter(createAdapterForTemp());
        SwitchOnTempSpinner.setOnItemSelectedListener(this);
        EconomySpinner = (Spinner) parent.usedView.findViewById(R.id.EconomyTypeSpinner);
        EconomySpinner.setOnItemSelectedListener(this);
        StartOnGasSwitch = (Switch) parent.usedView.findViewById(R.id.StartOnGasSwitch);
        StartOnGasSwitch.setOnCheckedChangeListener(this);
        ValveOpenSpinner = (Spinner) parent.usedView.findViewById(R.id.StartOnLPGOpenTimeSpinner);
        ValveOpenSpinner.setAdapter(createAdapterForValveOpen());
        ValveOpenSpinner.setOnItemSelectedListener(this);
        SensorTypeSpinner = (Spinner) parent.usedView.findViewById(R.id.LevelSensorTypeSpinner);
        SensorTypeSpinner.setOnItemSelectedListener(this);
        GasBenzinTimeSpinner = (Spinner) parent.usedView.findViewById(R.id.BensinGasTimeSpinner);
        GasBenzinTimeSpinner.setAdapter(createAdapterForGasBenzin());
        GasBenzinTimeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (actualDS == null)
            return;

        if (parent == SwitchOnTempSpinner) {
            // TODO: verify if its correct because its not :(
            int tt = BitUtils.GetTemperatureRaw(position);
            Log.d("Misc_worker", "Temperature set: " + tt);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x10, tt), 2));
        }
        if (parent == EconomySpinner) {
            switch (position) {
                case 0:
                    actualDS.setEconomyMode(KMEDataSettings.EconomyType.Normal);
                    break;
                case 1:
                    actualDS.setEconomyMode(KMEDataSettings.EconomyType.Eco);
                    break;
                case 2:
                    actualDS.setEconomyMode(KMEDataSettings.EconomyType.Sport);
                    break;
            }
            int raw = actualDS.getEconomyModeRaw();
            Log.d("Misc_worker", "EconomySpinner: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0A, raw), 2));
        }
        if (parent == SensorTypeSpinner) {
            switch (position) {
                case 0:
                    actualDS.setLevelSensor(KMEDataSettings.LevelSensorType.Ohm);
                    break;
                case 1:
                    actualDS.setLevelSensor(KMEDataSettings.LevelSensorType.Prog);
                    break;
                case 2:
                    actualDS.setLevelSensor(KMEDataSettings.LevelSensorType.Reserve);
                    break;
            }
            int raw = actualDS.getLevelSensorRaw();
            Log.d("Misc_worker", "SensorType: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        if (parent == ValveOpenSpinner) {
            actualDS.setStartOnGasOpenTime(position);
            int raw = actualDS.getStartOnGasOpenTimeRaw();
            Log.d("Misc_worker", "ValveOpenSpinner: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x26, raw), 2));
        }
        if (parent == GasBenzinTimeSpinner) {
            actualDS.setGasBenzineTime(position);
            int raw = actualDS.getGasBenzineTimeRaw();
            Log.d("Misc_worker", "GasBenzinTimeSpinner: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0F, raw), 2));
        }
        this.parent.refreshSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        actualDS = ds;
        TemperatureSensorEnabledSwitch.setOnCheckedChangeListener(null);
        TemperatureSensorEnabledSwitch.setChecked(ds.getTemperatureSensorEnabled());
        TemperatureSensorEnabledSwitch.setOnCheckedChangeListener(this);
        Utils.setSpinnerSelectionWithoutCallingListener(SwitchOnTempSpinner,
                BitUtils.GetTemperature(ds.getLPGOnTemperature() - 1));
        switch (ds.getEconomyMode())
        {
            case Normal:
                Utils.setSpinnerSelectionWithoutCallingListener(EconomySpinner, 0);
                break;
            case Eco:
                Utils.setSpinnerSelectionWithoutCallingListener(EconomySpinner, 1);
                break;
            case Sport:
                Utils.setSpinnerSelectionWithoutCallingListener(EconomySpinner, 2);
                break;
        }
        StartOnGasSwitch.setOnCheckedChangeListener(null);
        StartOnGasSwitch.setChecked(ds.getStartOnLPG());
        StartOnGasSwitch.setOnCheckedChangeListener(this);
        switch (ds.getLevelSensor())
        {
            case Ohm:
                Utils.setSpinnerSelectionWithoutCallingListener(SensorTypeSpinner, 0);
                break;
            case Prog:
                Utils.setSpinnerSelectionWithoutCallingListener(SensorTypeSpinner, 1);
                break;
            case Reserve:
                Utils.setSpinnerSelectionWithoutCallingListener(SensorTypeSpinner, 2);
                break;
        }
        Utils.setSpinnerSelectionWithoutCallingListener(ValveOpenSpinner,
                ds.getStartOnGasOpenTime());
        Utils.setSpinnerSelectionWithoutCallingListener(GasBenzinTimeSpinner,
                ds.getGasBenzineTime());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == TemperatureSensorEnabledSwitch) {
            SwitchOnTempSpinner.setEnabled(isChecked);
            actualDS.setTemperatureSensorEnabled(isChecked);
            int raw = actualDS.getTemperatureSensorEnabledRaw();
            Log.d("Misc_worker", "TemperatureSensorEnabledSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        if (buttonView == StartOnGasSwitch) {
            ValveOpenSpinner.setEnabled(isChecked);
            actualDS.setStartOnLPG(isChecked);
            int raw = actualDS.getStartOnLPGRaw();
            Log.d("Misc_worker", "StartOnGasSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0A, raw), 2));
        }
    }

    private ArrayAdapter<String> createAdapterForTemp() {
        List<String> temperatureStrings = new ArrayList<>();
        Integer[] availTemps = BitUtils.GetAvailTemperatures();
            /*Maximum temp is 110*/
        for (Integer availTemp : availTemps)
            temperatureStrings.add(String.valueOf(availTemp) + "°C");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, temperatureStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private ArrayAdapter<String> createAdapterForValveOpen() {
        List<String> temperatureStrings = new ArrayList<>();
        for(int i=0; i<=50; i++)
            temperatureStrings.add(String.valueOf(i/10) + "," + String.valueOf(i%10) + "s");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, temperatureStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private ArrayAdapter<String> createAdapterForGasBenzin() {
        List<String> temperatureStrings = new ArrayList<>();
        for(int i=0; i<=50; i++)
            temperatureStrings.add(String.valueOf(i/10) + "," + String.valueOf(i%10 )+ "s");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, temperatureStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}