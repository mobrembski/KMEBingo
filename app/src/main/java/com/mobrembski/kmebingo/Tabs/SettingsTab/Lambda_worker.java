package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.BluetoothController;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import java.util.ArrayList;
import java.util.List;

class Lambda_worker implements AdapterView.OnItemSelectedListener,
        RefreshViewsInterface
{
    private static final int LambdaRange1DefaultNeutralPoint = 22;
    private static final int LambdaRange2DefaultNeutralPoint = 60;
    private static final int LambdaRange3DefaultNeutralPoint = 127;
    private static final int LambdaRange1Offset = 20;
    private static final int LambdaRange2Offset = 50;
    private static final int LambdaRange3Offset = 100;
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
        LambdaNeutralPointSpinner.setOnItemSelectedListener(this);
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
        if (parent == LambdaTypeSpinner) {
            Log.d("Lambda_worker", "LambdaTypeSpinner: " + position);
            actualDS.setLambdaType(position);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x07, actualDS.getLambdaTypeRaw()), 2));
            if (position == 0)
                BluetoothController.getInstance().askForFrame(new KMEFrame(
                        BitUtils.packFrame(0x08, LambdaRange1DefaultNeutralPoint), 2));
            if (position == 5)
                BluetoothController.getInstance().askForFrame(new KMEFrame(
                        BitUtils.packFrame(0x08, LambdaRange2DefaultNeutralPoint), 2));
            if (position > 0 && position != 5)
                BluetoothController.getInstance().askForFrame(new KMEFrame(
                        BitUtils.packFrame(0x08, LambdaRange3DefaultNeutralPoint), 2));
        }
        if (parent == LambdaNeutralPointSpinner) {
            int rawVal = getNeutralPointSelectionToRaw(position);
            Log.d("Lambda_worker", "LambdaNeutralPointSpinner: " + position + " raw: " + rawVal);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x08, rawVal), 2));
        }
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

    private static int getNeutralPointSelectionFromRaw(KMEDataSettings ds) {
        int lambdaType = ds.getLambdaType();
        int neutralRaw = ds.getLambdaNeutralPoint();
        if (lambdaType == 5)
            return neutralRaw - LambdaRange2Offset;
        if (lambdaType == 0)
            return neutralRaw - LambdaRange1Offset;
        return neutralRaw - LambdaRange3Offset;
    }

    private int getNeutralPointSelectionToRaw(int spinnerPosition) {
        if (actualDS == null)
            return 0;
        int lambdaType = actualDS.getLambdaType();
        if (lambdaType == 5)
            return spinnerPosition + LambdaRange2Offset;
        if (lambdaType == 0)
            return spinnerPosition + LambdaRange1Offset;
        return spinnerPosition + LambdaRange3Offset;
    }

    public void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di) {
        actualDS = ds;
        int lambdaType = ds.getLambdaType();
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaTypeSpinner, lambdaType);
        LambdaNeutralPointSpinner.setAdapter(createAdapterForType(lambdaType));
        Utils.setSpinnerSelectionWithoutCallingListener(LambdaNeutralPointSpinner,
                getNeutralPointSelectionFromRaw(ds));
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