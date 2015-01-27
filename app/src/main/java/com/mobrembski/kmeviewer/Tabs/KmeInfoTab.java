package com.mobrembski.kmeviewer.Tabs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mobrembski.kmeviewer.BluetoothController;
import com.mobrembski.kmeviewer.ControllerEvent;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.RegistrationPlateChangeDialog;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataIdent;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;

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
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dpd = new DatePickerDialog(view.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        return v;
    }

    public void RegistrationChangeBtnClick(View v) {
        final RegistrationPlateChangeDialog dialog = new RegistrationPlateChangeDialog(
                getActivity(),
                this.dtn.RegistrationPlate);
        onConnectionStopping();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onConnectionStarting();
            }
        });
        dialog.show();
    }

    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null) {
            dtn = KMEDataInfo.GetDataFromByteArray(frame);
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) myView.findViewById(R.id.VersionValue);
                    tv.setText(ident.VersionString);
                    tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
                    tv.setText(String.valueOf(dtn.hoursOnGas + "h " + dtn.minutesOnGas + "min"));
                    tv = (TextView) myView.findViewById(R.id.RegistrationPlateValue);
                    tv.setText(dtn.RegistrationPlate);
                    tv = (TextView) myView.findViewById(R.id.DateOfInstallationValue);
                    tv.setText(dtn.DayOfInstallation + "-" + dtn.MonthOfInstallation + "-" + dtn.YearOfInstallation);
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
}
