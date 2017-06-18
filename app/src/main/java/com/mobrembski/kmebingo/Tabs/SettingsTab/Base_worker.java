package com.mobrembski.kmebingo.Tabs.SettingsTab;

import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.bluetoothmanager.ISerialConnectionManager;

public class Base_worker implements IRefreshSettingViews {

    protected ISerialConnectionManager btManager;
    protected boolean DataSettingLoaded = false;
    protected boolean DataConfigLoaded = false;
    protected boolean DataInfoLoaded = false;

    @Override
    public void setConnectionManager(ISerialConnectionManager btManager) {
        this.btManager = btManager;
    }

    @Override
    public void refreshViewsWhichDependsOnSettings(KMEDataSettings ds) {

    }

    @Override
    public void refreshViewsWhichDependsOnConfig(KMEDataConfig dc) {

    }

    @Override
    public void refreshViewsWhichDependsOnInfo(KMEDataInfo di) {

    }

    @Override
    public void refreshViewsWhichDependsOnActual(KMEDataActual da) {

    }
}
