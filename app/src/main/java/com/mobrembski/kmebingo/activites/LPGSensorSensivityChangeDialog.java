package com.mobrembski.kmebingo.activites;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.widget.Button;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SensorLevelView;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMESetDataFrame;
import com.mobrembski.kmebingo.bluetoothmanager.BluetoothConnectionManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class LPGSensorSensivityChangeDialog extends AppCompatDialog {
    private SensorLevelView level1;
    private SensorLevelView level2;
    private SensorLevelView level3;
    private SensorLevelView level4;
    private final SensorLevelView.OnSeekbarValueChanged seekbarChanged = new
            SensorLevelView.OnSeekbarValueChanged() {
                @Override
                public void callback(View v) {
                    // Funny, but it seems that java couldn't switch on View type
                    if (v == level1)
                        new changeSensivityTask().execute(1, level1.getSeekbarValue());
                    if (v == level2)
                        new changeSensivityTask().execute(2, level2.getSeekbarValue());
                    if (v == level3)
                        new changeSensivityTask().execute(3, level3.getSeekbarValue());
                    if (v == level4)
                        new changeSensivityTask().execute(4, level4.getSeekbarValue());
                }
            };
    private BluetoothConnectionManager btManager;

    public LPGSensorSensivityChangeDialog(Activity parentActivity, BluetoothConnectionManager btManager, int themeId) {
        super(parentActivity, themeId);
        // TODO: Why this is needed? Need to verify.
        setOwnerActivity(parentActivity);
        this.btManager = btManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lpgsensor_sensivity_dialog);
        setTitle(R.string.change_levels);
        Button okBtn = (Button) findViewById(R.id.LpgSensorSensivityDialogOkBtn);
        Button resetBtn = (Button) findViewById(R.id.LpgSensorSensivityDialogResetBtn);
        level1 = (SensorLevelView) findViewById(R.id.LpgSensorSensivityDialogSensorLevel1);
        level2 = (SensorLevelView) findViewById(R.id.LpgSensorSensivityDialogSensorLevel2);
        level3 = (SensorLevelView) findViewById(R.id.LpgSensorSensivityDialogSensorLevel3);
        level4 = (SensorLevelView) findViewById(R.id.LpgSensorSensivityDialogSensorLevel4);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new resetSensivityTask().execute();
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                closeDialog();
            }
        });
        level1.setOnProgressChangedListener(seekbarChanged);
        level2.setOnProgressChangedListener(seekbarChanged);
        level3.setOnProgressChangedListener(seekbarChanged);
        level4.setOnProgressChangedListener(seekbarChanged);
        btManager.postNewRequest(new KMEDataInfo(), 1);
        EventBus.getDefault().register(this);
    }

    private void closeDialog() {
        EventBus.getDefault().unregister(this);
        dismiss();
    }

    @Subscribe
    public void onEvent(final KMEDataInfo info) {
        getOwnerActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                level1.setSeekbarValue(info.SensorLevel1);
                level2.setSeekbarValue(info.SensorLevel2);
                level3.setSeekbarValue(info.SensorLevel3);
                level4.setSeekbarValue(info.SensorLevel4);

                switch (info.LevelIndicatorOn) {
                    case 4:
                        level4.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level3.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level2.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level1.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        break;
                    case 3:
                        level4.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level3.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level2.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level1.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        break;
                    case 2:
                        level4.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level3.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level2.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        level1.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        break;
                    case 1:
                        level4.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level3.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level2.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level1.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenON));
                        break;
                    case 0:
                        level4.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level3.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level2.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        level1.getValueImage().setBackgroundColor(getOwnerActivity().getResources().getColor(R.color.IndicatorGreenOFF));
                        break;
                }
            }
        });
        btManager.postNewRequest(new KMEDataInfo(), 1);
    }

    private class resetSensivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            btManager.postNewRequest(new KMESetDataFrame(BitUtils.packFrame(0x22, 40), 2), 1);
            btManager.postNewRequest(new KMESetDataFrame(BitUtils.packFrame(0x23, 95), 2), 1);
            btManager.postNewRequest(new KMESetDataFrame(BitUtils.packFrame(0x24, 137), 2), 1);
            btManager.postNewRequest(new KMESetDataFrame(BitUtils.packFrame(0x25, 205), 2), 1);
            return null;
        }

    }

    private class changeSensivityTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            // params[1] is number of seekbar. - 1 for better using (Seekbar1 will be 1, not 0 etc.)
            btManager.postNewRequest(
                    new KMESetDataFrame(BitUtils.packFrame(0x22 + (byte)(params[0] - 1), params[1]), 2), 1);
            return null;
        }

    }
}
