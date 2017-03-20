package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobrembski.kmebingo.ExpandableRowView;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.Tabs.KMEViewerTab;
import com.mobrembski.kmebingo.activites.FactoryResetDialog;
import com.mobrembski.kmebingo.activites.MainActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class KMESettingsTab extends KMEViewerTab {
    private final List<IRefreshSettingViews> views = new ArrayList<>();
    View usedView;

    public KMESettingsTab() {
        super();
        this.layoutId = R.layout.settings_tab;
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
                FactoryResetDialog dialog = new FactoryResetDialog(getActivity(), btManager);
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

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity mainActivity = (MainActivity) getActivity();
        this.btManager = mainActivity.btManager;
        for(IRefreshSettingViews view : views) {
            view.setBluetoothManager(btManager);
        }
        sendRequestsToDevice();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final KMEDataInfo info) {
        for(IRefreshSettingViews bw : views) {
            bw.refreshViewsWhichDependsOnInfo(info);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final KMEDataConfig config) {
        for(IRefreshSettingViews bw : views) {
            bw.refreshViewsWhichDependsOnConfig(config);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final KMEDataSettings settings) {
        for(IRefreshSettingViews bw : views) {
            bw.refreshViewsWhichDependsOnSettings(settings);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(final KMEDataActual actual) {
        for(IRefreshSettingViews bw : views) {
            bw.refreshViewsWhichDependsOnActual(actual);
        }
    }

    public void sendRequestsToDevice() {
        btManager.postNewRequest(new KMEDataActual(), 1);
        btManager.postNewRequest(new KMEDataSettings(), 1);
        btManager.postNewRequest(new KMEDataConfig(), 1);
    }
}
