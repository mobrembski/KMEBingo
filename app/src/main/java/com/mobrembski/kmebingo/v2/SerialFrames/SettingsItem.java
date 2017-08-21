package com.mobrembski.kmebingo.v2.SerialFrames;

import com.mobrembski.kmebingo.v2.BitUtils;

public class SettingsItem {

    private final int offset;
    private int value;
    private final int size;
    private SettingsRow myRow;

    public SettingsItem(int size, int offset) {
        this(0, size, offset);
    }

    public SettingsItem(int value, int size, int offset) {
        this.value = value;
        this.size = size;
        this.offset = offset;
    }

    public void SetValue(int value) {
        this.value = value;
    }

    public void SetValue(boolean value) {
        this.value = value ? 1:0;
    }

    public void SetRow(SettingsRow row) {
        this.myRow = row;
    }

    public int GetValue() {
        return this.value;
    }

    public int GetSize() {
        return this.size;
    }

    public int GetOffset() {
        return this.offset;
    }

    public boolean GetValueBool() {
        return this.value != 0;
    }

    public static int GenerateRawByte(SettingsItem[] tab) {
        int ret = 0;
        for (SettingsItem item : tab) {
            ret |= item.GetValue() << item.offset;
        }
        return ret;
    }

    public int GenerateRawByte() {
        if (myRow == null)
            return 0;
        return myRow.GenerateRawByte();
    }

    public static void SetFromRawByte(SettingsItem[] tab, int val) {
        for (SettingsItem aTab : tab) {
            int mask;
            SettingsItem item = aTab;
            if (item.size > 1)
                mask = BitUtils.PowerOf2(item.offset + item.size) - 1;
            else
                mask = BitUtils.PowerOf2(item.offset);
            int value = (val & mask) >> item.offset;
            item.SetValue(value);
        }
    }



}
