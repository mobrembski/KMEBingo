package com.mobrembski.kmebingo.Tabs.SettingsTab;


import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
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

class Ignition_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final KMESettingsTab parent;
    private final Spinner IgnitionTypeSpinner;
    private final Spinner LPGSwitchingPointSpinner;
    private final RadioButton FallingRadioButton;
    private final RadioButton RisingRadioButton;
    private final Switch LowRPMSignalSwitch;
    private final Switch CutOFFEnabledSwitch;
    private final Spinner LoweringToRPMSSpinner;
    private final Spinner LoweringToPWASpinner;
    private final Switch HighRPMLimitSwitch;
    private final Spinner HighRPMLimitSpinner;
    private final ArrayAdapter<String> rpmAdapter;
    private final ArrayAdapter<String> pwaAdapter;
    private KMEDataSettings actualDS;
    private KMEDataConfig actualDC;

    public Ignition_worker(KMESettingsTab parent) {
        this.parent = parent;
        rpmAdapter = createRpmsAdapter();
        pwaAdapter = createPWALoweringAdapter();
        IgnitionTypeSpinner = (Spinner) parent.usedView.findViewById(R.id.IgnitionTypeSpinner);
        IgnitionTypeSpinner.setOnItemSelectedListener(this);
        LPGSwitchingPointSpinner = (Spinner) parent.usedView.findViewById(R.id.LPGSwitchingPointSpinner);
        LPGSwitchingPointSpinner.setAdapter(rpmAdapter);
        LPGSwitchingPointSpinner.setOnItemSelectedListener(this);
        FallingRadioButton = (RadioButton) parent.usedView.findViewById(R.id.FallingRPMSRadio);
        RisingRadioButton = (RadioButton) parent.usedView.findViewById(R.id.RisingRPMSRadio);
        FallingRadioButton.setOnClickListener(this);
        RisingRadioButton.setOnClickListener(this);
        LowRPMSignalSwitch = (Switch) parent.usedView.findViewById(R.id.LowRPMSingalLevelSwitch);
        LowRPMSignalSwitch.setOnCheckedChangeListener(this);
        CutOFFEnabledSwitch = (Switch) parent.usedView.findViewById(R.id.CutOFFEnabledSwitch);
        CutOFFEnabledSwitch.setOnCheckedChangeListener(this);
        LoweringToRPMSSpinner = (Spinner) parent.usedView.findViewById(R.id.MinimalCutOFFRPMSSpinner);
        LoweringToRPMSSpinner.setAdapter(rpmAdapter);
        LoweringToRPMSSpinner.setOnItemSelectedListener(this);
        LoweringToPWASpinner = (Spinner) parent.usedView.findViewById(R.id.LoweringPWASpinner);
        LoweringToPWASpinner.setAdapter(createPWALoweringAdapter());
        LoweringToPWASpinner.setOnItemSelectedListener(this);
        HighRPMLimitSwitch = (Switch) parent.usedView.findViewById(R.id.HighRPMLimitSwitch);
        HighRPMLimitSwitch.setOnCheckedChangeListener(this);
        HighRPMLimitSpinner = (Spinner) parent.usedView.findViewById(R.id.HighRPMLimitSpinner);
        HighRPMLimitSpinner.setOnItemSelectedListener(this);
        HighRPMLimitSpinner.setAdapter(rpmAdapter);
    }

    private ArrayAdapter<String> createPWALoweringAdapter() {
        List<String> pwaValues = new ArrayList<>();
        for(int i=2; i<=100 ;i+=2)
            pwaValues.add(String.valueOf(i));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, pwaValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private ArrayAdapter<String> createRpmsAdapter() {
        List<String> rpmsStrings = new ArrayList<>();
        for(Integer rpm : BitUtils.GetAvailRpms())
            rpmsStrings.add(String.valueOf(rpm));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, rpmsStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Ignition_worker", "onItemSelected" + view + parent);
        if (parent == IgnitionTypeSpinner) {
            actualDC.IgnitionType.SetValue(position + 1);
            int raw = actualDC.IgnitionType.GenerateRawByte();
            Log.d("Ignition_worker", "IgnitionTypeSpinner: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x18, raw), 2));
        }
        if (parent == LoweringToPWASpinner) {
            int raw = (position * 2) + 2;
            Log.d("Ignition_worker", "LoweringToPWASpinner:"+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x1B, raw), 2));
        }
        if (parent == LPGSwitchingPointSpinner) {
            Log.d("Ignition_worker", "LPGSwitchingPointSpinner");
            int selectedRPMS = Integer.parseInt(rpmAdapter.getItem(position));
            int rawRpm[] = BitUtils.GetRPMToRaw(selectedRPMS);
            Log.d("Ignition_worker", "LPGSwitchingPointSpinner raw LSB: "+rawRpm[1]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x16, rawRpm[1]), 2));
            Log.d("Ignition_worker", "LPGSwitchingPointSpinner raw MSB: "+rawRpm[0]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x17, rawRpm[0]), 2));
        }
        if (parent == LoweringToRPMSSpinner) {
            Log.d("Ignition_worker", "LoweringToRPMSSpinner");
            int selectedRPMS = Integer.parseInt(rpmAdapter.getItem(position));
            int rawRpm[] = BitUtils.GetRPMToRaw(selectedRPMS);
            Log.d("Ignition_worker", "LoweringToRPMSSpinner raw LSB: "+rawRpm[1]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x19, rawRpm[1]), 2));
            Log.d("Ignition_worker", "LoweringToRPMSSpinner raw MSB: "+rawRpm[0]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x1A, rawRpm[0]), 2));
        }
        if (parent == HighRPMLimitSpinner) {
            Log.d("Ignition_worker", "HighRPMLimitSpinner");
            int selectedRPMS = Integer.parseInt(rpmAdapter.getItem(position));
            int rawRpm[] = BitUtils.GetRPMToRaw(selectedRPMS);
            Log.d("Ignition_worker", "HighRPMLimitSpinner raw LSB: "+rawRpm[1]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x1C, rawRpm[1]), 2));
            Log.d("Ignition_worker", "HighRPMLimitSpinner raw MSB: "+rawRpm[0]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x1D, rawRpm[0]), 2));
        }
        this.parent.refreshSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        actualDS = ds;
        actualDC = dc;
        if (ds.getTurnOnAtIncreasingRPM()) {
            FallingRadioButton.setChecked(false);
            RisingRadioButton.setChecked(true);
        }
        else {
            FallingRadioButton.setChecked(true);
            RisingRadioButton.setChecked(false);
        }
        CutOFFEnabledSwitch.setChecked(ds.getCutOffEnabled());
        LoweringToPWASpinner.setEnabled(ds.getCutOffEnabled());
        LoweringToRPMSSpinner.setEnabled(ds.getCutOffEnabled());
        HighRPMLimitSwitch.setChecked(ds.getCutOffHighRPMEnabled());
        HighRPMLimitSpinner.setEnabled(ds.getCutOffHighRPMEnabled());
        LowRPMSignalSwitch.setChecked(ds.getLowRPMSignalLevel());
        LowRPMSignalSwitch.setEnabled(true);
        setSpinnerRPMValue(HighRPMLimitSpinner,
                dc.HighRPMLimit2.GetValue(), dc.HighRPMLimit1.GetValue());
        setSpinnerRPMValue(LPGSwitchingPointSpinner,
                dc.SwitchOnLPGRPM2.GetValue(), dc.SwitchOnLPGRPM1.GetValue());
        setSpinnerRPMValue(LoweringToRPMSSpinner,
                dc.MinimalCutoffRPMS2.GetValue(), dc.MinimalCutoffRPMS1.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(IgnitionTypeSpinner,
                dc.IgnitionType.GetValue() - 1);
        Utils.setSpinnerSelectionWithoutCallingListener(LoweringToPWASpinner,
                pwaAdapter.getPosition(String.valueOf(dc.CutoffMixtureImpoverishment.GetValue())));
    }

    private void setSpinnerRPMValue(Spinner spinner, int valueHigh, int valueLow) {
        int rpm = BitUtils.GetRPMFromRaw(valueHigh << 8 | valueLow);
        Utils.setSpinnerSelectionWithoutCallingListener(spinner,
                rpmAdapter.getPosition(String.valueOf(rpm)));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == LowRPMSignalSwitch) {
            LowRPMSignalSwitch.setChecked(isChecked);
            actualDS.setLowRPMSignalLevel(isChecked);
            int raw = actualDS.getLowRPMSingalLevelRaw();
            Log.d("Ignition_worker", "LowRPMSignalSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0A, raw), 2));
        }
        if (buttonView == CutOFFEnabledSwitch) {
            LoweringToRPMSSpinner.setEnabled(isChecked);
            LoweringToPWASpinner.setEnabled(isChecked);
            actualDS.setCutOffEnabled(isChecked);
            int raw = actualDS.getCutOffEnabledRaw();
            Log.d("Ignition_worker", "CutOFFEnabledSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        if (buttonView == HighRPMLimitSwitch) {
            HighRPMLimitSpinner.setEnabled(isChecked);
            actualDS.setCutOffHighRPMEnabled(isChecked);
            int raw = actualDS.getCutOffHighRPMEnabledRaw();
            Log.d("Ignition_worker", "HighRPMLimitSwitch: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == FallingRadioButton || v == RisingRadioButton) {
            boolean turnOnIncreasingRPM;
            if (v == FallingRadioButton) {
                Log.d("Ignition_worker", "FallingRadioButton");
                FallingRadioButton.setChecked(true);
                RisingRadioButton.setChecked(false);
                turnOnIncreasingRPM = false;
            }
            else {
                Log.d("Ignition_worker", "RisingRadioButton");
                RisingRadioButton.setChecked(true);
                FallingRadioButton.setChecked(false);
                turnOnIncreasingRPM = true;
            }
            actualDS.setTurnOnAtIncreasingRPM(turnOnIncreasingRPM);
            int raw = actualDS.getTurnOnAtIncreasingRPMRaw();
            Log.d("Ignition_worker", "TurnOnIncreasingRPM: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x06, raw), 2));
        }
        this.parent.refreshSettings();
    }
}