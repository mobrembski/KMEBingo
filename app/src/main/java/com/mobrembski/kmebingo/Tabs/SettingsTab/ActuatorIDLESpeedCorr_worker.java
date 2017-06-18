package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMESetDataFrame;

class ActuatorIDLESpeedCorr_worker extends Base_worker implements AdapterView.OnItemSelectedListener {
    private final KMESettingsTab parent;
    private final Spinner OpeningCorrectionSpinner;
    private final Spinner ClosingCorrectionSpinner;
    private KMEDataInfo actualDi;

    public ActuatorIDLESpeedCorr_worker(KMESettingsTab parent) {
        this.parent = parent;
        OpeningCorrectionSpinner = (Spinner) parent.usedView.findViewById(R.id.SpeedCorrectionOpeningIdleSpinner);
        OpeningCorrectionSpinner.setOnItemSelectedListener(this);
        OpeningCorrectionSpinner.setAdapter(Utils.createActuatorSpeedStepsAdapter(18, parent.usedView));
        ClosingCorrectionSpinner = (Spinner) parent.usedView.findViewById(R.id.SpeedCorrectionClosingIdleSpinner);
        ClosingCorrectionSpinner.setOnItemSelectedListener(this);
        ClosingCorrectionSpinner.setAdapter(Utils.createActuatorSpeedStepsAdapter(20, parent.usedView));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (actualDi == null) return;
        if (!DataInfoLoaded) return;
        if (parent == OpeningCorrectionSpinner) {
            actualDi.ActuatorSpeedIdleOpeningCorrection.SetValue(position);
            int raw = actualDi.ActuatorSpeedIdleOpeningCorrection.GenerateRawByte();
            Log.d("ActuatorIdleSpeedCorr", "OpeningCorrectionSpinner: " + raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x0D, raw), 2));
        }
        if (parent == ClosingCorrectionSpinner) {
            actualDi.ActuatorSpeedIdleClosingCorrection.SetValue(position);
            int raw = actualDi.ActuatorSpeedIdleClosingCorrection.GenerateRawByte();
            Log.d("ActuatorIdleSpeedCorr", "ClosingCorrectionSpinner: "+raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x0D, raw), 2));
        }
        this.parent.sendRequestsToDevice();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void refreshViewsWhichDependsOnInfo(KMEDataInfo di) {
        actualDi = di;
        Utils.setSpinnerSelectionWithoutCallingListener(OpeningCorrectionSpinner,
                di.ActuatorSpeedIdleOpeningCorrection.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(ClosingCorrectionSpinner,
                di.ActuatorSpeedIdleClosingCorrection.GetValue());
        DataInfoLoaded = true;
    }
}