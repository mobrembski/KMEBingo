package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;
import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

public class LPGSensorSensivityChangeDialog extends Dialog {
    private SensorLevelView level1;
    private SensorLevelView level2;
    private SensorLevelView level3;
    private SensorLevelView level4;
    private final SensorLevelView.OnSeekbarValueChanged seekbarChanged = new
            SensorLevelView.OnSeekbarValueChanged() {
                @Override
                public void callback(View v) {
                    // Funny, but it seems that java couldn't switch on View type
                    if (v == level1) {
                        BluetoothController.getInstance().askForFrame(new KMEFrame(
                                BitUtils.packFrame(0x22, level1.getSeekbarValue()), 2));
                    }
                    if (v == level2) {
                        BluetoothController.getInstance().askForFrame(new KMEFrame(
                                BitUtils.packFrame(0x23, level2.getSeekbarValue()), 2));
                    }
                    if (v == level3) {
                        BluetoothController.getInstance().askForFrame(new KMEFrame(
                                BitUtils.packFrame(0x24, level3.getSeekbarValue()), 2));
                    }
                    if (v == level4) {
                        BluetoothController.getInstance().askForFrame(new KMEFrame(
                                BitUtils.packFrame(0x25, level4.getSeekbarValue()), 2));
                    }
                }
            };
    private Thread askingThread;
    private boolean runThread = true;
    private final Runnable askingRunnable = new Runnable() {
        @Override
        public void run() {
            while (runThread) {
                if (BluetoothController.getInstance().IsConnected()) {
                    KMEDataInfo info = new KMEDataInfo(BluetoothController.getInstance()
                            .askForFrame(new KMEDataInfo()));
                    updateScreen(info);
                }
            }
        }
    };

    public LPGSensorSensivityChangeDialog(Activity parentActivity) {
        super(parentActivity);
        // TODO: Why this is needed? Need to verify.
        setOwnerActivity(parentActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lpgsensor_sensivity_dialog);
        setTitle("Change levels");
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
        askingThread = new Thread(askingRunnable);
        askingThread.start();
        level1.setOnProgressChangedListener(seekbarChanged);
        level2.setOnProgressChangedListener(seekbarChanged);
        level3.setOnProgressChangedListener(seekbarChanged);
        level4.setOnProgressChangedListener(seekbarChanged);
    }

    private void closeDialog() {
        runThread = false;
        if (askingThread != null) {
            try {
                askingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        dismiss();
    }

    private void updateScreen(final KMEDataInfo info) {
        Activity activity = getOwnerActivity();
        if (activity != null)
            activity.runOnUiThread(new Runnable() {
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
    }

    private class resetSensivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x22, 40), 2));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x23, 95), 2));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x24, 137), 2));
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x25, 205), 2));
            return null;
        }

    }
}
