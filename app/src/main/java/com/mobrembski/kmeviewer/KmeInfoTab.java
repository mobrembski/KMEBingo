package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.OtherSettingsFrame;

public class KmeInfoTab extends KMEViewerTab implements ControllerEvent {
    private KMEDataInfo dtn;

    public KmeInfoTab() {
        this.layoutId = R.layout.kmeinfotab;
        this.askFrame = new OtherSettingsFrame();
        super.setAskFrame(askFrame);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        Button registrationChangeBtn = (Button) v.findViewById(R.id.button);
        registrationChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistrationChangeBtnClick(view);
            }
        });
        return v;
    }

    public void RegistrationChangeBtnClick(View v) {
        final RegistrationPlateChangeDialog dialog = new RegistrationPlateChangeDialog(
                getActivity(),
                this.dtn.RegistrationPlate);
        onConnectionStopping();
        final KmeInfoTab tab = this;
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
                    TextView tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
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
}
