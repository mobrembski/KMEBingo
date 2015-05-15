package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.BluetoothController;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

@SuppressWarnings("ALL")
class TPS_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface
{
    private KMESettingsTab parent;
    private Spinner TPSTypeSpinner;
    private Spinner TPSInertness;
    private KMEDataSettings actualDS;

    public TPS_worker(KMESettingsTab parent) {
        this.parent = parent;
        TPSTypeSpinner = (Spinner) parent.usedView.findViewById(R.id.TpsTypeSpinner);
        TPSTypeSpinner.setOnItemSelectedListener(this);
        TPSInertness = (Spinner) parent.usedView.findViewById(R.id.TpsInertnessSpinner);
        TPSInertness.setOnItemSelectedListener(this);
        TPSInertness.setAdapter(Utils.createActuatorStepsArrayAdapter(parent.usedView));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == TPSTypeSpinner) {
            actualDS.setTPSType(position);
            int raw = actualDS.getTPSTypeRaw();
            Log.d("TPS_worker", "TPSTypeSpinner: " + raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x07, raw), 2));
        }
        if (parent == TPSInertness) {
            actualDS.setTPSInertness(position);
            int raw = actualDS.getTPSInertnessRaw();
            Log.d("TPS_worker", "TPSInertness: "+raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x1E, raw), 2));
        }
        this.parent.refreshSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        actualDS = ds;
        Utils.setSpinnerSelectionWithoutCallingListener(TPSTypeSpinner, ds.getTPSType());
        Utils.setSpinnerSelectionWithoutCallingListener(TPSInertness, ds.getTPSInertness());
    }
}