package com.mobrembski.kmebingo.Tabs.SettingsTab;

import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.bluetoothmanager.BluetoothConnectionManager;

public class Base_worker implements IRefreshSettingViews {

    protected BluetoothConnectionManager btManager;

    @Override
    public void setBluetoothManager(BluetoothConnectionManager btManager) {
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
