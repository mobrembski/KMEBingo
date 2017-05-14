package com.mobrembski.kmebingo.Tabs.SettingsTab;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
            view.setConnectionManager(btManager);
        }
        sendRequestsToDevice();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d("DebugTab", "KMESettingsTab visible");
            sendRequestsToDevice();
        } else {
            Log.d("DebugTab", "KMESettingsTab not visible");
        }
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
        if (btManager == null) return;
        btManager.postNewRequest(new KMEDataActual(), 1);
        btManager.postNewRequest(new KMEDataSettings(), 1);
        btManager.postNewRequest(new KMEDataConfig(), 1);
    }
}
