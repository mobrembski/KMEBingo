package com.mobrembski.kmebingo.v2.SerialFrames;

import com.mobrembski.kmebingo.v2.BitUtils;

import java.util.ArrayList;
import java.util.List;

public class SettingsRow {
    private List<SettingsItem> listInRow = new ArrayList<>();

    public SettingsRow() {
    }

    public void addToRow(SettingsItem item) {
        listInRow.add(item);
        item.SetRow(this);
    }

    public void makeRow(SettingsItem[] tab) {
        for(SettingsItem item : tab) {
            listInRow.add(item);
            item.SetRow(this);
        }
    }

    public static SettingsRow makeSettingsRow(SettingsItem[] tab) {
        SettingsRow ret = new SettingsRow();
        ret.makeRow(tab);
        return ret;
    }

    public List<SettingsItem> GetItemList() {
        return listInRow;
    }

    public void SetFromRawByte(int val) {
        for (int i=0; i<listInRow.size(); i++) {
            int mask;
            SettingsItem item = listInRow.get(i);
            if(item.GetSize() > 1)
                mask = BitUtils.PowerOf2(item.GetOffset() + item.GetSize()) - 1;
            else
                mask = BitUtils.PowerOf2(item.GetOffset());
            int value = (val & mask) >> item.GetOffset();
            item.SetValue(value);
        }
    }

    public int GenerateRawByte() {
        int ret = 0;
        for (SettingsItem item : listInRow) {
            ret |= item.GetValue() << item.GetOffset();
        }
        return ret;
    }
}
