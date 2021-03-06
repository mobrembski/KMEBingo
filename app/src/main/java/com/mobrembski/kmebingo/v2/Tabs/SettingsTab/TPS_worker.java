package com.mobrembski.kmebingo.v2.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mobrembski.kmebingo.v2.BitUtils;
import com.mobrembski.kmebingo.v2.R;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.v2.SerialFrames.KMESetDataFrame;

@SuppressWarnings("ALL")
class TPS_worker extends Base_worker implements AdapterView.OnItemSelectedListener {
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
        if (actualDS == null) return;
        if (!DataSettingLoaded) return;
        if (parent == TPSTypeSpinner) {
            actualDS.setTPSType(position);
            int raw = actualDS.getTPSTypeRaw();
            Log.d("TPS_worker", "TPSTypeSpinner: " + raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x07, raw), 2));
        }
        if (parent == TPSInertness) {
            actualDS.setTPSInertness(position);
            int raw = actualDS.getTPSInertnessRaw();
            Log.d("TPS_worker", "TPSInertness: "+raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x1E, raw), 2));
        }
        this.parent.sendInitialRequestsToDevice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshViewsWhichDependsOnSettings(KMEDataSettings ds) {
        actualDS = ds;
        Utils.setSpinnerSelectionWithoutCallingListener(TPSTypeSpinner, ds.getTPSType());
        Utils.setSpinnerSelectionWithoutCallingListener(TPSInertness, ds.getTPSInertness());
        DataSettingLoaded = true;
    }
}