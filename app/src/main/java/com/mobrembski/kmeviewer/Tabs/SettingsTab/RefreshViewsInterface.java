package com.mobrembski.kmeviewer.Tabs.SettingsTab;

import com.mobrembski.kmeviewer.SerialFrames.KMEDataConfig;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataInfo;
import com.mobrembski.kmeviewer.SerialFrames.KMEDataSettings;

interface RefreshViewsInterface {
    void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di);
}
