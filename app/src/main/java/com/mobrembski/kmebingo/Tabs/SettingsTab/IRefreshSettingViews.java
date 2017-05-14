package com.mobrembski.kmebingo.Tabs.SettingsTab;

import com.mobrembski.kmebingo.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.bluetoothmanager.ISerialConnectionManager;

interface IRefreshSettingViews {
    void refreshViewsWhichDependsOnSettings(KMEDataSettings ds);
    void refreshViewsWhichDependsOnConfig(KMEDataConfig dc);
    void refreshViewsWhichDependsOnInfo(KMEDataInfo di);
    void refreshViewsWhichDependsOnActual(KMEDataActual da);
    void setConnectionManager(ISerialConnectionManager manager);
}
