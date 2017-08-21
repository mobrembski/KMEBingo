package com.mobrembski.kmebingo.v2.Tabs.SettingsTab;

import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataActual;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.v2.SerialFrames.KMEDataSettings;
import com.mobrembski.kmebingo.v2.bluetoothmanager.ISerialConnectionManager;

interface IRefreshSettingViews {
    void refreshViewsWhichDependsOnSettings(KMEDataSettings ds);
    void refreshViewsWhichDependsOnConfig(KMEDataConfig dc);
    void refreshViewsWhichDependsOnInfo(KMEDataInfo di);
    void refreshViewsWhichDependsOnActual(KMEDataActual da);
    void setConnectionManager(ISerialConnectionManager manager);
}
