package com.mobrembski.kmebingo.Tabs.SettingsTab;

import com.mobrembski.kmebingo.SerialFrames.KMEDataConfig;
import com.mobrembski.kmebingo.SerialFrames.KMEDataInfo;
import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;

interface RefreshViewsInterface {
    void refreshValue(KMEDataSettings ds, KMEDataConfig dc, KMEDataInfo di);
}
