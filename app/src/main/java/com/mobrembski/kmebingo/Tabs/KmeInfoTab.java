package com.mobrembski.kmebingo.Tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mobrembski.kmebingo.BitUtils;
import com.mobrembski.kmebingo.BluetoothController;
import com.mobrembski.kmebingo.ControllerEvent;
import com.mobrembski.kmebingo.LPGSensorSensivityChangeDialog;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataIdent;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEFrame;

import java.util.Calendar;

public class KmeInfoTab extends KMEViewerTab implements ControllerEvent {
    private KMEDataInfo dtn;
    private KMEDataIdent ident;
    private Button registrationChangeBtn;
    private Button installationDateChangeBtn;
    private Button changeRunningTimeBtn;
    private Button changeSensorLevelLevelsBtn;

    public KmeInfoTab() {
        this.layoutId = R.layout.info_tab;
        this.askFrame = new KMEDataInfo();
        super.setAskFrame(askFrame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        registrationChangeBtn = (Button) v.findViewById(R.id.ChangeRegPlateBtn);
        installationDateChangeBtn = (Button) v.findViewById(R.id.ChangeInstallDateBtn);
        changeRunningTimeBtn = (Button) v.findViewById(R.id.ChangeRunningCounterBtn);
        changeSensorLevelLevelsBtn = (Button) v.findViewById(R.id.ChangeSensorLevelBtn);
        registrationChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrationChangeBtnClick(view);
            }
        });
        installationDateChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installationDateChangeOpenDialog(view);
            }
        });
        changeRunningTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeRunningCounterBtnClick(view);
            }
        });
        changeSensorLevelLevelsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LPGSensorSensivityChangeDialog dialog = new LPGSensorSensivityChangeDialog(getActivity());
                dialog.show();
            }
        });
        if (BluetoothController.getInstance().IsConnected())
            ident = new KMEDataIdent(BluetoothController.getInstance()
                    .askForFrame(new KMEDataIdent()));
        return v;
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null) {
            dtn = new KMEDataInfo(frame);
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) myView.findViewById(R.id.ControllerTypeValue);
                    if (ident.ControllerType == KMEDataIdent.BingoType.BingoM)
                        tv.setText("Bingo M");
                    else
                        tv.setText("Bingo S");
                    tv = (TextView) myView.findViewById(R.id.VersionValue);
                    tv.setText(ident.VersionString);
                    tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
                    tv.setText(String.valueOf(dtn.hoursOnGas + "h " + dtn.minutesOnGas + "min"));
                    tv = (TextView) myView.findViewById(R.id.RegistrationPlateValue);
                    tv.setText(dtn.RegistrationPlate);
                    tv = (TextView) myView.findViewById(R.id.DateOfInstallationValue);
                    tv.setText(dtn.DayOfInstallation + "-" + dtn.MonthOfInstallation + "-" +
                            dtn.YearOfInstallation);
                    tv = (TextView) myView.findViewById(R.id.TankLevelValue);
                    switch (dtn.LevelIndicatorOn) {
                        case 4:
                            tv.setText("100%");
                            break;
                        case 3:
                            tv.setText("75%");
                            break;
                        case 2:
                            tv.setText("50%");
                            break;
                        case 1:
                            tv.setText("25%");
                            break;
                        case 0:
                            tv.setText("LOW LEVEL");
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onConnectionStarting() {
        // We don't have to update ident. It's just hardcoded
        // into controller, so let's ask for ident only after connecting.
        ident = new KMEDataIdent(BluetoothController.getInstance()
                .askForFrame(new KMEDataIdent()));
        super.onConnectionStarting();
    }

    @Override
    public void onConnectionStopping() {
        super.onConnectionStopping();
    }

    private void changeRunningCounterBtnClick(View v) {
        View npView = View.inflate(v.getContext(), R.layout.running_counter_change_dialog, null);
        final NumberPicker hoursPicker = (NumberPicker) npView.findViewById(R.id.RunningHoursPicker);
        hoursPicker.setMaxValue(65535);
        hoursPicker.setMinValue(0);
        hoursPicker.setValue(dtn.hoursOnGas);
        final NumberPicker minutesPicker = (NumberPicker) npView.findViewById(
                R.id.RunningMinutesPicker);
        minutesPicker.setMaxValue(60);
        minutesPicker.setMinValue(0);
        minutesPicker.setValue(dtn.minutesOnGas);
        new AlertDialog.Builder(v.getContext())
                .setView(npView)
                .setTitle("Change running time")
                .setMessage("Please enter running time")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new changeRunningTimeTask().execute(hoursPicker.getValue(),
                                minutesPicker.getValue());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void registrationChangeBtnClick(View v) {
        final EditText inputText = new EditText(v.getContext());
        inputText.setText(this.dtn.RegistrationPlate);
        inputText.setGravity(Gravity.CENTER);
        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        onConnectionStopping();
        new AlertDialog.Builder(v.getContext())
                .setTitle("Change registration")
                .setMessage("Enter registration number")
                .setView(inputText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newRegNumber = inputText.getText().toString();
                        //changeRegistrationTask will handle starting again asking thread
                        new changeRegistrationTask().execute(newRegNumber);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConnectionStarting();
                    }
                })
                .show();
    }

    private void installationDateChangeOpenDialog(View v) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        onConnectionStopping();
        DatePickerDialog dpd = new DatePickerDialog(v.getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {
                        new changeInstallationDateTask().execute(year, month, day);
                    }
                }, mYear, mMonth, mDay);
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onConnectionStarting();
            }
        });
        dpd.setTitle("Change installation date");
        dpd.show();
    }

    //region Tasks
    private class changeRegistrationTask extends AsyncTask<String, Integer, Void> {
        ProgressDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onConnectionStopping();
            waitDialog = new ProgressDialog(myView.getContext());
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(7);
            waitDialog.setMessage("Changing..");
            waitDialog.setTitle("Registration...");
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            char tab[] = params[0].toCharArray();
            for (int i = 0; i < tab.length; i++) {
                BluetoothController.getInstance().askForFrame(new KMEFrame(
                        BitUtils.packFrame(0x2B + i, tab[i]), 2));
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            onConnectionStarting();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }

    private class changeRunningTimeTask extends AsyncTask<Integer, Integer, Void> {
        ProgressDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onConnectionStopping();
            waitDialog = new ProgressDialog(myView.getContext());
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(3);
            waitDialog.setMessage("Changing..");
            waitDialog.setTitle("Running time...");
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            char[] tab = BitUtils.GetRawRunningCounter(params[0], params[1]);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x27, tab[1]), 2));
            publishProgress(1);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x28, tab[2]), 2));
            publishProgress(2);
            BluetoothController.getInstance().askForFrame(new KMEFrame(
                    BitUtils.packFrame(0x32, tab[0]), 2));
            publishProgress(2);
            try {
                Thread.sleep(100);
                publishProgress(2);
                Thread.sleep(100);
                publishProgress(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            onConnectionStarting();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }

    private class changeInstallationDateTask extends AsyncTask<Integer, Integer, Void> {
        ProgressDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onConnectionStopping();
            waitDialog = new ProgressDialog(myView.getContext());
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(2);
            waitDialog.setMessage("Changing..");
            waitDialog.setTitle("Installation date...");
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            char tab[] = BitUtils.GetRawDate(params[0], params[1], params[2]);
            KMEFrame DateFrame1 = new KMEFrame(BitUtils.packFrame(0x29, tab[0]), 2);
            KMEFrame DateFrame2 = new KMEFrame(BitUtils.packFrame(0x2A, tab[1]), 2);
            BluetoothController.getInstance().askForFrame(DateFrame1);
            BluetoothController.getInstance().askForFrame(DateFrame2);
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            onConnectionStarting();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }
    //endregion
}
