package com.mobrembski.kmeviewer.Tabs.SettingsTab;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobrembski.kmeviewer.BluetoothController;
import com.mobrembski.kmeviewer.ControllerEvent;
import com.mobrembski.kmeviewer.ExpandableRowView;
import com.mobrembski.kmeviewer.FactoryResetDialog;
import com.mobrembski.kmeviewer.R;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataConfig;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataSettings;
import com.mobrembski.kmeviewer.Tabs.KMEViewerTab;

import java.util.ArrayList;
import java.util.List;

public class KMESettingsTab extends KMEViewerTab implements ControllerEvent {
    private final List<RefreshViewsInterface> views = new ArrayList<>();
    View usedView;

    public KMESettingsTab() {
        this.layoutId = R.layout.settings_tab;
        super.setAskFrame(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        LayoutInflater ownInflater = (LayoutInflater) getActivity().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        usedView = myView;
        Button factoryResetBtn = (Button) myView.findViewById(R.id.factoryResetBtn);
        factoryResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FactoryResetDialog dialog = new FactoryResetDialog(getActivity());
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                dialog.show();
            }
        });
        ExpandableRowView lambdaRow = (ExpandableRowView) myView.findViewById(R.id.LambdaSettingsRow);
        ExpandableRowView actuatorRow = (ExpandableRowView) myView.findViewById(R.id.ActuatorSettingsRow);
        ExpandableRowView tpsRow = (ExpandableRowView) myView.findViewById(R.id.TPSSettingsRow);
        ExpandableRowView rpmsRow = (ExpandableRowView) myView.findViewById(R.id.RPMsSettingsRow);
        ExpandableRowView miscRow = (ExpandableRowView) myView.findViewById(R.id.MiscSettingsRow);

        ViewGroup parent = lambdaRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.settings_tab_lambda_row, parent);
        parent = actuatorRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.settings_tab_actuator_row, parent);
        parent = tpsRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.settings_tab_tps_row, parent);
        parent = rpmsRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.settings_tab_rpms_row, parent);
        parent = miscRow.getInjectHiddenView();
        ownInflater.inflate(R.layout.settings_tab_misc_row, parent);

        views.add(new Lambda_worker(this));
        views.add(new Actuator_worker(this));
        views.add(new ActuatorIDLESpeedCorr_worker(this));
        views.add(new ActuatorLOADSpeedCorr_worker(this));
        views.add(new TPS_worker(this));
        views.add(new Ignition_worker(this));
        views.add(new Misc_worker(this));

        refreshSettings();
        return v;
    }

    public void packetReceived(final int[] frame) {

    }

    public void refreshSettings() {
        BluetoothController controller = BluetoothController.getInstance();
        KMEDataSettings actualSettings = new KMEDataSettings();
        KMEDataConfig actualConfig = new KMEDataConfig();
        KMEDataInfo actualInfo = new KMEDataInfo();
        if (controller.IsConnected()) {
            actualSettings = new KMEDataSettings(controller.askForFrame(new KMEDataSettings()));
            actualConfig = new KMEDataConfig(controller.askForFrame(new KMEDataConfig()));
            actualInfo = new KMEDataInfo(controller.askForFrame(new KMEDataInfo()));
        }

        for (RefreshViewsInterface view : views)
            view.refreshValue(actualSettings, actualConfig, actualInfo);
    }
}
