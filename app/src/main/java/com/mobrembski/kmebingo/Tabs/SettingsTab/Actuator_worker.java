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
import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import org.jraf.android.backport.switchwidget.Switch;

import java.util.ArrayList;
import java.util.List;

import static com.mobrembski.kmebingo.BitUtils.GetVoltage;

class Actuator_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface, CompoundButton.OnCheckedChangeListener
{
    private final KMESettingsTab parent;
    private final Spinner PWAValueSpinner;
    private final Switch PWAEnabledSwitch;
    private final Spinner MinimalOpenIdleSpinner;
    private final Spinner MaximumOpenIdleSpinner;
    private final Spinner MinimalOpenLoadSpinner;
    private final Spinner MaximumOpenLoadSpinner;
    private final Spinner DesiredATTPosSpinner;
    private final Spinner TPSSenseSpinner;
    private final Switch SetATTPosSwitch;
    private KMEDataSettings actualDS;

    public Actuator_worker(KMESettingsTab parent) {
        this.parent = parent;
        PWAEnabledSwitch = (Switch) parent.usedView.findViewById(R.id.SetPWAValueSwitch);
        PWAEnabledSwitch.setOnCheckedChangeListener(this);
        PWAValueSpinner = (Spinner) parent.usedView.findViewById(R.id.SetPWAValueSpinner);
        PWAValueSpinner.setOnItemSelectedListener(this);
        PWAValueSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter(parent.usedView));
        MinimalOpenIdleSpinner = (Spinner) parent.usedView.findViewById(R.id.MinimalOpenOnIDLESpinner);
        MaximumOpenIdleSpinner = (Spinner) parent.usedView.findViewById(R.id.MaximumOpenOnIDLESpinner);
        MinimalOpenIdleSpinner.setOnItemSelectedListener(this);
        MaximumOpenIdleSpinner.setOnItemSelectedListener(this);
        MinimalOpenIdleSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter("-", parent.usedView));
        MaximumOpenIdleSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter("+", parent.usedView));
        MinimalOpenLoadSpinner = (Spinner) parent.usedView.findViewById(R.id.MinimalOpenOnLOADSpinner);
        MaximumOpenLoadSpinner = (Spinner) parent.usedView.findViewById(R.id.MaximumOpenOnLOADSpinner);
        MinimalOpenLoadSpinner.setOnItemSelectedListener(this);
        MaximumOpenLoadSpinner.setOnItemSelectedListener(this);
        MinimalOpenLoadSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter("-", parent.usedView));
        MaximumOpenLoadSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter("+", parent.usedView));
        SetATTPosSwitch = (Switch) parent.usedView.findViewById(R.id.SetATTPosSwitch);
        SetATTPosSwitch.setOnCheckedChangeListener(this);
        TPSSenseSpinner = (Spinner) parent.usedView.findViewById(R.id.TPSSenseLevelSpinner);
        TPSSenseSpinner.setOnItemSelectedListener(this);
        TPSSenseSpinner.setAdapter(createTPSenseAdapter());
        DesiredATTPosSpinner = (Spinner) parent.usedView.findViewById(R.id.DesiredATTPosSpinner);
        DesiredATTPosSpinner.setOnItemSelectedListener(this);
        DesiredATTPosSpinner.setAdapter(Utils.createActuatorStepsArrayAdapter(parent.usedView));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == PWAValueSpinner) {
            Log.d("Actuator_worker", "PWAValueSpinner: " + position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x13, position), 2));
        }
        if (parent == MinimalOpenIdleSpinner) {
            Log.d("Actuator_worker", "MinimalOpenIdleSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x21, position), 2));
        }
        if (parent == MaximumOpenIdleSpinner) {
            Log.d("Actuator_worker", "MaximumOpenIdleSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x20, position), 2));
        }
        if (parent == MinimalOpenLoadSpinner) {
            Log.d("Actuator_worker", "MinimalOpenLoadSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x12, position), 2));
        }
        if (parent == MaximumOpenLoadSpinner) {
            Log.d("Actuator_worker", "MaximumOpenLoadSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x11, position), 2));
        }
        if (parent == TPSSenseSpinner) {
            Log.d("Actuator_worker", "TPSSenseSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x14, position), 2));
        }
        if (parent == DesiredATTPosSpinner) {
            Log.d("Actuator_worker", "DesiredATTPosSpinner: "+position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x15, position), 2));
        }
        this.parent.refreshSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        Log.d("Actuator_worker", "refresh");
        actualDS = ds;
        PWAEnabledSwitch.setOnCheckedChangeListener(null);
        PWAEnabledSwitch.setChecked(ds.getPWAEnabled());
        PWAEnabledSwitch.setOnCheckedChangeListener(this);
        PWAValueSpinner.setEnabled(ds.getPWAEnabled());
        Log.d("Actuator_worker", String.valueOf(ds.getATTEnabled()));

        int PWAValue = new KMEDataActual(
                BluetoothController.getInstance().askForFrame(new KMEDataActual())).PWA;
        Utils.setSpinnerSelectionWithoutCallingListener(PWAValueSpinner, PWAValue);

        Utils.setSpinnerSelectionWithoutCallingListener(MinimalOpenIdleSpinner,
                dc.ActuatorMinOpenOnIdle.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(MaximumOpenIdleSpinner,
                dc.ActuatorMaxOpenOnIdle.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(MinimalOpenLoadSpinner,
                dc.ActuatorMinOpenOnLoad.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(MaximumOpenLoadSpinner,
                dc.ActuatorMaxOpenOnLoad.GetValue());

        SetATTPosSwitch.setOnCheckedChangeListener(null);
        SetATTPosSwitch.setChecked(ds.getATTEnabled());
        SetATTPosSwitch.setOnCheckedChangeListener(this);
        DesiredATTPosSpinner.setEnabled(ds.getATTEnabled());
        TPSSenseSpinner.setEnabled(ds.getATTEnabled());
        Utils.setSpinnerSelectionWithoutCallingListener(DesiredATTPosSpinner,
                dc.ATTEnrichFuelMixture.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(TPSSenseSpinner,
                dc.ATTTPSSenseLevel.GetValue());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == PWAEnabledSwitch) {
            PWAValueSpinner.setEnabled(isChecked);
            actualDS.setPWAEnabled(!isChecked);
            int raw = actualDS.getPWAEnabledRaw();
            Log.d("Actuator_worker", "PWAEnabledSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        if (buttonView == SetATTPosSwitch) {
            TPSSenseSpinner.setEnabled(isChecked);
            DesiredATTPosSpinner.setEnabled(isChecked);
            actualDS.setATTEnabled(isChecked);
            int raw = actualDS.getATTEnabledRaw();
            Log.d("Actuator_worker", "SetATTPosSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        this.parent.refreshSettings();
    }

    private ArrayAdapter<String> createTPSenseAdapter() {
        List<String> actuatorStepsStrings = new ArrayList<>();
        for(int i=0; i<=255; i++)
            actuatorStepsStrings.add(String.valueOf(GetVoltage(i)) + " V");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, actuatorStepsStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}