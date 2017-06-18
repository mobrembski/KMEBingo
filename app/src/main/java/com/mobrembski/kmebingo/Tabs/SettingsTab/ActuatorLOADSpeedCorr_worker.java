package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMESetDataFrame;

class ActuatorLOADSpeedCorr_worker extends Base_worker implements AdapterView.OnItemSelectedListener {
    private final KMESettingsTab parent;
    private final Spinner OpeningCorrectionSpinner;
    private final Spinner ClosingCorrectionSpinner;
    private KMEDataInfo actualDi;

    public ActuatorLOADSpeedCorr_worker(KMESettingsTab parent) {
        this.parent = parent;
        OpeningCorrectionSpinner = (Spinner) parent.usedView.findViewById(R.id.SpeedCorrectionOpeningLoadSpinner);
        OpeningCorrectionSpinner.setOnItemSelectedListener(this);
        OpeningCorrectionSpinner.setAdapter(Utils.createActuatorSpeedStepsAdapter(10, parent.usedView));
        ClosingCorrectionSpinner = (Spinner) parent.usedView.findViewById(R.id.SpeedCorrectionClosingLoadSpinner);
        ClosingCorrectionSpinner.setOnItemSelectedListener(this);
        ClosingCorrectionSpinner.setAdapter(Utils.createActuatorSpeedStepsAdapter(14, parent.usedView));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (actualDi == null) return;
        if (!DataInfoLoaded) return;
        if (parent == OpeningCorrectionSpinner) {
            actualDi.ActuatorSpeedLoadOpeningCorrection.SetValue(position);
            int raw = actualDi.ActuatorSpeedLoadOpeningCorrection.GenerateRawByte();
            Log.d("ActuatorLOADSpeedCorr", "OpeningCorrectionSpinner: " + raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x0E, raw), 2));
        }
        if (parent == ClosingCorrectionSpinner) {
            actualDi.ActuatorSpeedLoadClosingCorrection.SetValue(position);
            int raw = actualDi.ActuatorSpeedLoadClosingCorrection.GenerateRawByte();
            Log.d("ActuatorLOADSpeedCorr", "ClosingCorrectionSpinner: "+raw);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x0E, raw), 2));
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
                di.ActuatorSpeedLoadOpeningCorrection.GetValue());
        Utils.setSpinnerSelectionWithoutCallingListener(ClosingCorrectionSpinner,
                di.ActuatorSpeedLoadClosingCorrection.GetValue());
        DataInfoLoaded = true;
    }
}