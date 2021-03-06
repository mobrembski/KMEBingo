package com.mobrembski.kmebingo.v2.Tabs;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.mobrembski.kmebingo.v2.BitUtils;
import com.mobrembski.kmebingo.v2.R;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataIdent;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.v2.SerialFrames.KMESetDataFrame;
import com.mobrembski.kmebingo.v2.activites.LPGSensorSensivityChangeDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;

public class KMEInfoTab extends KMEViewerTab {
    private Button registrationChangeBtn;
    private Button installationDateChangeBtn;
    private Button changeRunningTimeBtn;
    private Button changeSensorLevelLevelsBtn;
    private KMEDataInfo currentDataInfo;
    private TypedValue dialogStyleTypedValue;
    private int progressDialogStyleResId;

    public KMEInfoTab() {
        this.layoutId = R.layout.info_tab;
        currentDataInfo = new KMEDataInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        dialogStyleTypedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.dialog_style, dialogStyleTypedValue, true);

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
                sensorSensivityDialog();
            }
        });
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.progress_dialog_style, typedValue, true);
        progressDialogStyleResId = typedValue.resourceId;
        return v;
    }

    @Override
    protected void sendInitialRequestsToDevice() {
        if (btManager == null) return;
        btManager.postNewRequest(new KMEDataInfo(), 1);
        btManager.postNewRequest(new KMEDataIdent(), 1);
    }

    @Subscribe
    public void onEvent(final KMEDataIdent ident) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) myView.findViewById(R.id.ControllerTypeValue);
                if (ident.ControllerType == KMEDataIdent.BingoType.BingoM)
                    tv.setText("Bingo M");
                else
                    tv.setText("Bingo S");
                tv = (TextView) myView.findViewById(R.id.VersionValue);
                tv.setText(ident.VersionString);
            }
        });
    }

    @Subscribe
    public void onEvent(final KMEDataInfo info) {
        this.currentDataInfo = info;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
                tv.setText(String.valueOf(info.hoursOnGas + "h " + info.minutesOnGas + "min"));
                tv = (TextView) myView.findViewById(R.id.RegistrationPlateValue);
                tv.setText(info.RegistrationPlate);
                tv = (TextView) myView.findViewById(R.id.DateOfInstallationValue);
                tv.setText(info.DayOfInstallation + "-" + info.MonthOfInstallation + "-" +
                        info.YearOfInstallation);
                tv = (TextView) myView.findViewById(R.id.TankLevelValue);
                switch (info.LevelIndicatorOn) {
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
                        tv.setText(getString(R.string.low_level));
                        break;
                }
            }
        });
    }

    private void changeRunningCounterBtnClick(View v) {
        if(!checkIfBTManagerIsConnected()) return;
        View npView = View.inflate(v.getContext(), R.layout.running_counter_change_dialog, null);
        final NumberPicker hoursPicker = (NumberPicker) npView.findViewById(R.id.RunningHoursPicker);
        hoursPicker.setMaxValue(65535);
        hoursPicker.setMinValue(0);
        hoursPicker.setValue(currentDataInfo.hoursOnGas);
        final NumberPicker minutesPicker = (NumberPicker) npView.findViewById(
                R.id.RunningMinutesPicker);
        minutesPicker.setMaxValue(60);
        minutesPicker.setMinValue(0);
        minutesPicker.setValue(currentDataInfo.minutesOnGas);
        new AlertDialog.Builder(v.getContext(), dialogStyleTypedValue.resourceId)
                .setView(npView)
                .setTitle(R.string.change_running_time)
                .setMessage(R.string.please_enter_running_time)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new changeRunningTimeTask().execute(hoursPicker.getValue(),
                                minutesPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void registrationChangeBtnClick(View v) {
        if(!checkIfBTManagerIsConnected()) return;
        final EditText inputText = new EditText(v.getContext());
        inputText.setText(this.currentDataInfo.RegistrationPlate);
        inputText.setGravity(Gravity.CENTER);
        inputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
        new AlertDialog.Builder(v.getContext())
                .setTitle(R.string.change_registration)
                .setMessage(R.string.enter_registration)
                .setView(inputText)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newRegNumber = inputText.getText().toString();
                        new changeRegistrationTask().execute(newRegNumber);
                    }
                })
                .setNegativeButton(R.string.cancel_button, null)
                .show();
    }

    private void installationDateChangeOpenDialog(View v) {
        if(!checkIfBTManagerIsConnected()) return;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.progress_dialog_style, typedValue, true);
        DatePickerDialog dpd = new DatePickerDialog(v.getContext(), typedValue.resourceId,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int month, int day) {
                        new changeInstallationDateTask().execute(year, month, day);
                    }
                }, mYear, mMonth, mDay);
        dpd.setTitle(R.string.change_installation_date);
        dpd.show();
    }

    private void sensorSensivityDialog() {
        if(!checkIfBTManagerIsConnected()) return;
        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.sensors_dialog_style, typedValue, true);
        LPGSensorSensivityChangeDialog dialog =
                new LPGSensorSensivityChangeDialog(getActivity(), btManager, typedValue.resourceId);
        dialog.show();
    }

    private boolean checkIfBTManagerIsConnected() {
        if (btManager == null || !btManager.isConnected()) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.cannot_change_without_connection)
                    .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
            return false;
        }
        return true;
    }

    //region Tasks
    private class changeRegistrationTask extends AsyncTask<String, Integer, Void> {
        ProgressDialog waitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            waitDialog = new ProgressDialog(myView.getContext(), progressDialogStyleResId);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(7);
            waitDialog.setMessage(getString(R.string.changing));
            waitDialog.setTitle(R.string.registration);
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            char tab[] = params[0].toCharArray();
            for (int i = 0; i < tab.length; i++) {
                btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x2B + i, tab[i]), 2));
                publishProgress(i + 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            sendInitialRequestsToDevice();
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
            waitDialog = new ProgressDialog(myView.getContext(), progressDialogStyleResId);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(3);
            waitDialog.setMessage(getString(R.string.changing));
            waitDialog.setTitle(R.string.running_time);
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            char[] tab = BitUtils.GetRawRunningCounter(params[0], params[1]);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x27, tab[1]), 2));
            publishProgress(1);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x28, tab[2]), 2));
            publishProgress(2);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x32, tab[0]), 2));
            publishProgress(3);
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            sendInitialRequestsToDevice();
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
            waitDialog = new ProgressDialog(myView.getContext(), progressDialogStyleResId);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setMax(2);
            waitDialog.setMessage(getString(R.string.changing));
            waitDialog.setTitle(R.string.installation_date);
            waitDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            char tab[] = BitUtils.GetRawDate(params[0], params[1], params[2]);
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x29, tab[0]), 2));
            btManager.runRequestNow(new KMESetDataFrame(BitUtils.packFrame(0x2A, tab[1]), 2));
            return null;
        }

        @Override
        protected void onPostExecute(Void notUsed) {
            super.onPostExecute(notUsed);
            waitDialog.dismiss();
            sendInitialRequestsToDevice();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }
    //endregion
}
