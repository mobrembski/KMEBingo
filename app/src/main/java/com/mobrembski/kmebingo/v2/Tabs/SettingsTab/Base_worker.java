package com.mobrembski.kmebingo.v2.Tabs.SettingsTab;

import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.v2.bluetoothmanager.ISerialConnectionManager;

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
