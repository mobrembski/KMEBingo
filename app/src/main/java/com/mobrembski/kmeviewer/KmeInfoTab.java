package com.mobrembski.kmeviewer;

import android.app.Activity;
import android.widget.TextView;

import com.mobrembski.kmeviewer.SerialFrames.AskFrameClass;
import com.mobrembski.kmeviewer.SerialFrames.OtherSettingsFrame;

public class KmeInfoTab extends KMEViewerTab {
    public KmeInfoTab() {
        this.layoutId = R.layout.kmeinfotab;
        final AskFrameClass askFrame = new AskFrameClass(new OtherSettingsFrame(), this);
        super.setAskFrame(askFrame);
    }

    @Override
    public void packetReceived(final int[] frame) {
        Activity main = getActivity();
        if (main != null && frame != null)
            main.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    KMEDataInfo dtn = KMEDataInfo.GetDataFromByteArray(frame);
                    TextView tv = (TextView) myView.findViewById(R.id.TimeOnGasValue);
                    tv.setText(String.valueOf(dtn.hoursOnGas+"h "+dtn.minutesOnGas+"min"));
                    tv = (TextView) myView.findViewById(R.id.RegistrationPlateValue);
                    tv.setText(dtn.RegistrationPlate);
                    tv = (TextView) myView.findViewById(R.id.DateOfInstallationValue);
                    tv.setText(dtn.DayOfInstallation+"-"+dtn.MonthOfInstallation+"-"+dtn.YearOfInstallation);
                    tv = (TextView) myView.findViewById(R.id.TankLevelValue);
                    switch (dtn.LevelIndicatorOn)
                    {
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
