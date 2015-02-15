package com.mobrembski.kmeviewer.Tabs;

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
import android.widget.TextView;

import com.mobrembski.kmeviewer.BitUtils;
import com.mobrembski.kmeviewer.BluetoothController;
import com.mobrembski.kmeviewer.ControllerEvent;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataIdent;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;
import com.mobrembski.kmeviewer.SerialFrames.KMEFrame;

import java.util.Calendar;

public class KmeInfoTab extends KMEViewerTab implements ControllerEvent {
    private KMEDataInfo dtn;
    private KMEDataIdent ident;

    public KmeInfoTab() {
        this.layoutId = R.layout.kme_info_tab;
        this.askFrame = new KMEDataInfo();
        super.setAskFrame(askFrame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        assert v != null;
        Button registrationChangeBtn = (Button) v.findViewById(R.id.ChangeRegPlateBtn);
        Button installationDateChangeBtn = (Button) v.findViewById(R.id.ChangeInstallDateBtn);
        registrationChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationChangeBtnClick(view);
            }
        });
        installationDateChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InstallationDateChangeOpenDialog(view);
            }
        });
        return v;
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null) {
            dtn = KMEDataInfo.GetDataFromByteArray(frame);
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
        ident = KMEDataIdent.GetDataFromByteArray(
                BluetoothController.getInstance()
                        .askForFrame(new KMEDataIdent()));
        super.onConnectionStarting();
    }

    private void RegistrationChangeBtnClick(View v) {
        final EditText inputText = new EditText(v.getContext());
        inputText.setText(this.dtn.RegistrationPlate);
        inputText.setGravity(Gravity.CENTER);
        inputText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(7)});
        onConnectionStopping();
        new AlertDialog.Builder(v.getContext())
                .setTitle("Registration Change")
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

    private void InstallationDateChangeOpenDialog(View v) {
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
                        changeInstallationDate(year, month, day);
                        onConnectionStarting();

                    }
                }, mYear, mMonth, mDay);
        dpd.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                onConnectionStarting();
            }
        });
        dpd.show();
    }

    private void changeInstallationDate(int year, int month, int day) {
        char tab[] = BitUtils.GetRawDate(year, month, day);
        KMEFrame DateFrame1 = new KMEFrame(BitUtils.packFrame(0x29, tab[0]), 2);
        KMEFrame DateFrame2 = new KMEFrame(BitUtils.packFrame(0x2A, tab[1]), 2);
        BluetoothController.getInstance().askForFrame(DateFrame1);
        BluetoothController.getInstance().askForFrame(DateFrame2);
    }

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
            waitDialog.setTitle("Registration change...");
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
            super.onPreExecute();
            waitDialog.dismiss();
            onConnectionStarting();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            this.waitDialog.setProgress(progress[0]);
        }
    }
}
