package com.mobrembski.kmeviewer.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mobrembski.kmeviewer.BitUtils;
import com.mobrembski.kmeviewer.BluetoothController;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataConfig;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataSettings;
import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

import java.util.ArrayList;
import java.util.List;

class Lambda_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface
{
    private final Spinner LambdaNeutralPointSpinner;
    private final Spinner LambdaTypeSpinner;
    private final Spinner LambdaDelaySpinner;
    private final Spinner LambdaEmulationTypeSpinner;
    private final Spinner LambdaEmulationHStateSpinner;
    private final Spinner LambdaEmulationLStateSpinner;
    private final KMESettingsTab parent;
    private KMEDataSettings actualDS;

    public Lambda_worker(KMESettingsTab parent) {
        this.parent = parent;
        LambdaTypeSpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaTypeSpinner);
        LambdaTypeSpinner.setOnItemSelectedListener(this);
        LambdaNeutralPointSpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaNeutralPointSpinner);
        LambdaNeutralPointSpinner.setAdapter(createAdapterForType(0));
        LambdaDelaySpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaDelaySpinner);
        ArrayAdapter<String> adapterDelayTime = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, generateTimeList());
        adapterDelayTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        LambdaDelaySpinner.setAdapter(adapterDelayTime);
        LambdaDelaySpinner.setOnItemSelectedListener(this);
        LambdaEmulationHStateSpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaEmulationHStateSpinner);
        LambdaEmulationLStateSpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaEmulationLStateSpinner);
        LambdaEmulationHStateSpinner.setOnItemSelectedListener(this);
        LambdaEmulationLStateSpinner.setOnItemSelectedListener(this);
        LambdaEmulationLStateSpinner.setAdapter(createAdapterForEmulationState());
        LambdaEmulationHStateSpinner.setAdapter(createAdapterForEmulationState());
        LambdaEmulationTypeSpinner = (Spinner) parent.usedView.findViewById(R.id.LambdaEmulationTypeSpinner);
        LambdaEmulationTypeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == LambdaDelaySpinner) {
            Log.d("Lambda_worker", "LambdaDelaySpinner: " + (position + 1));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x09, position + 1), 2));
        }
        if (parent == LambdaEmulationTypeSpinner) {
            switch (position) {
                case 0:
                    actualDS.setLambdaEmulationType(
                            KMEDataSettings.LambdaEmulationType.Course);
                    break;
                case 1:
                    actualDS.setLambdaEmulationType(
                            KMEDataSettings.LambdaEmulationType.Ground);
                    break;
                case 2:
                    actualDS.setLambdaEmulationType(
                            KMEDataSettings.LambdaEmulationType.Disconnected);
                    break;
            }
            int raw = actualDS.getLambdaEmulationTypeRaw();
            Log.d("Lambda_worker", "LambdaEmulationTypeSpinner: " + raw);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0A, raw), 2));
        }

        if (parent == LambdaEmulationHStateSpinner) {
            Log.d("Lambda_worker", "LambdaEmulationHStateSpinner: " + (position+1));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0B, position + 1), 2));
        }
        if (parent == LambdaEmulationLStateSpinner) {
            Log.d("Lambda_worker", "LambdaEmulationLStateSpinner: " + (position+1));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x0C, position + 1), 2));
        }
        this.parent.refreshSettings();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private ArrayAdapter<CharSequence> createAdapterForType(int lambdaType) {
        int arrayRes = R.array.lambda_neutral_point_0v_1v;
        if (lambdaType > 0 && lambdaType != 5 )
            arrayRes = R.array.lambda_neutral_point_high;
        if (lambdaType == 5)
            arrayRes = R.array.lambda_neutral_point_low;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                parent.usedView.getContext(),
                arrayRes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private ArrayAdapter<String> createAdapterForEmulationState() {
        List<String> emulationTimes = new ArrayList<>();
        for (int i=1; i<=255; i++) {
            int tmp = 25 * i;
            int dec = tmp % 1000;
            int dec2 = tmp / 1000;
            if (tmp < 100)
                emulationTimes.add(String.valueOf(dec2+",0"+dec+" s"));
            else
                emulationTimes.add(String.valueOf(dec2+","+dec+" s"));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.usedView.getContext(),
                android.R.layout.simple_spinner_item, emulationTimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        actualDS = ds;
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaTypeSpinner, ds.getLambdaType());
        // TODO: solve problem with different values for different lambdatypes
        //LambdaNeutralPointSpinner.setSelection(ds.getLambdaNeutralPoint());
        // LambdaState & LambdaDelay is counted from 1, not from 0 (0 is illegal value)
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaDelaySpinner, ds.getLambdaDelay() - 1);
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaEmulationHStateSpinner,
                ds.getLambdaEmulationHState() - 1);
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaEmulationLStateSpinner,
                ds.getLambdaEmulationLState() - 1);
        switch (ds.getLambdaEmulationType())
        {
            case Course:
                Utils.setSpinnerSelectionWithoutCallingListener(LambdaEmulationTypeSpinner, 0);
                LambdaEmulationHStateSpinner.setEnabled(true);
                LambdaEmulationLStateSpinner.setEnabled(true);
                break;
            case Ground:
                Utils.setSpinnerSelectionWithoutCallingListener(LambdaEmulationTypeSpinner, 1);
                LambdaEmulationHStateSpinner.setEnabled(false);
                LambdaEmulationLStateSpinner.setEnabled(false);
                break;
            case Disconnected:
                Utils.setSpinnerSelectionWithoutCallingListener(LambdaEmulationTypeSpinner, 2);
                LambdaEmulationHStateSpinner.setEnabled(false);
                LambdaEmulationLStateSpinner.setEnabled(false);
                break;
        }
    }

    private List<String> generateTimeList() {
        List<String> ret = new ArrayList<>();
        for (int i = 5; i <= 255 * 5; i+=5)
            ret.add(String.valueOf(i / 60) + "min " + String.valueOf(i % 60) + "s");
        return ret;
    }
}