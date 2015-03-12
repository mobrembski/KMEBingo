package com.mobrembski.kmeviewer.tests;

import com.mobrembski.kmeviewer.SerialFrames.SettingsItem;
import com.mobrembski.kmeviewer.SerialFrames.SettingsRow;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SettingsItemTest {

    private final SettingsRow row1 = new SettingsRow();
    private final SettingsRow row2 = new SettingsRow();
    private final SettingsRow row3 = new SettingsRow();
    private final SettingsRow row4 = new SettingsRow();
    private final SettingsItem _1bit_1 = new SettingsItem(1,0);
    private final SettingsItem _1bit_2 = new SettingsItem(1,1);
    private final SettingsItem _1bit_3 = new SettingsItem(1,2);
    private final SettingsItem _1bit_4 = new SettingsItem(1,3);
    private final SettingsItem _1bit_5 = new SettingsItem(1,4);
    private final SettingsItem _1bit_6 = new SettingsItem(1,5);
    private final SettingsItem _1bit_7 = new SettingsItem(1,6);
    private final SettingsItem _1bit_8 = new SettingsItem(1,7);
    private final SettingsItem _1bit_9 = new SettingsItem(1,2);
    private final SettingsItem _1bit_10 = new SettingsItem(1,3);
    private final SettingsItem _2bit_1 = new SettingsItem(2,0);
    private final SettingsItem _2bit_2 = new SettingsItem(2,4);
    private final SettingsItem _2bit_3 = new SettingsItem(2,6);
    private final SettingsItem _4bit_1 = new SettingsItem(4,0);
    private final SettingsItem _4bit_2 = new SettingsItem(4,4);
    private final SettingsItem _8bit_1 = new SettingsItem(8,0);

    @Before
    public void setUp() throws Exception {
        row1.makeRow(new SettingsItem[]{_1bit_1, _1bit_2, _1bit_3, _1bit_4, _1bit_5, _1bit_6, _1bit_7, _1bit_8});
        row2.makeRow(new SettingsItem[]{_4bit_1, _4bit_2});
        row3.makeRow(new SettingsItem[]{_8bit_1});
        row4.makeRow(new SettingsItem[]{_2bit_1, _1bit_9, _1bit_10, _2bit_2, _2bit_3});
    }

    @Test
    public void testSetValue() throws Exception {
        SettingsItem item;
        item = new SettingsItem(8,0,1);
        item.SetValue(255);
        Assert.assertEquals(255, item.GetValue());
        item.SetValue(128);
        Assert.assertEquals(128, item.GetValue());
        item.SetValue(27);
        Assert.assertEquals(27, item.GetValue());

        item = new SettingsItem(3,0,1);
        item.SetValue(255);
        Assert.assertEquals(255, item.GetValue());

        item.SetValue(true);
        Assert.assertEquals(1, item.GetValue());

        item.SetValue(false);
        Assert.assertEquals(0, item.GetValue());

    }

    @Test
    public void testGetValue() throws Exception {
        SettingsItem item = new SettingsItem(244,8,0);
        Assert.assertEquals(244, item.GetValue());
    }

    @Test
    public void testFromRawByteAll1bit() throws Exception {
        row1.SetFromRawByte(255);
        Assert.assertEquals(1, _1bit_1.GetValue());
        Assert.assertEquals(1, _1bit_5.GetValue());
        Assert.assertEquals(1, _1bit_3.GetValue());
        Assert.assertEquals(0xFF, _1bit_1.GenerateRawByte());

        row1.SetFromRawByte(4);
        Assert.assertEquals(0, _1bit_1.GetValue());
        Assert.assertEquals(0, _1bit_5.GetValue());
        Assert.assertEquals(1, _1bit_3.GetValue());
        Assert.assertEquals(0x04, _1bit_2.GenerateRawByte());

        _1bit_2.SetValue(true);
        Assert.assertEquals(0x06, _1bit_1.GenerateRawByte());

        row1.SetFromRawByte(255);
        for(SettingsItem item : row1.GetItemList())
            Assert.assertEquals(1, item.GetValue());

        row1.SetFromRawByte(128);
        List<SettingsItem> tmpItemList = row1.GetItemList();
        for(int i=0;i<tmpItemList.size(); i++) {
            if(i==7)
                Assert.assertEquals(1, tmpItemList.get(i).GetValue());
            else
                Assert.assertEquals(0, tmpItemList.get(i).GetValue());
        }

        row1.SetFromRawByte(65);
        for(int i=0;i<tmpItemList.size(); i++) {
            if(i==0 || i==6)
                Assert.assertEquals(1, tmpItemList.get(i).GetValue());
            else
                Assert.assertEquals(0, tmpItemList.get(i).GetValue());
        }
    }

    @Test
    public void testFromRawByteAll4bit() throws Exception {
        row2.SetFromRawByte(255);
        Assert.assertEquals(15, _4bit_1.GetValue());
        Assert.assertEquals(15, _4bit_2.GetValue());
        row2.SetFromRawByte(15);
        Assert.assertEquals(15, _4bit_1.GetValue());
        Assert.assertEquals(0, _4bit_2.GetValue());
        row2.SetFromRawByte(16);
        Assert.assertEquals(0, _4bit_1.GetValue());
        Assert.assertEquals(1, _4bit_2.GetValue());
        row2.SetFromRawByte(17);
        Assert.assertEquals(1, _4bit_1.GetValue());
        Assert.assertEquals(1, _4bit_2.GetValue());
        row2.SetFromRawByte(18);
        Assert.assertEquals(2, _4bit_1.GetValue());
        Assert.assertEquals(1, _4bit_2.GetValue());
        row2.SetFromRawByte(128);
        Assert.assertEquals(0, _4bit_1.GetValue());
        Assert.assertEquals(8, _4bit_2.GetValue());
    }

    @Test
    public void testFromRawByteAll8bit() throws Exception {
        row3.SetFromRawByte(255);
        Assert.assertEquals(255, _8bit_1.GetValue());
        row3.SetFromRawByte(128);
        Assert.assertEquals(128, _8bit_1.GetValue());
        row3.SetFromRawByte(1);
        Assert.assertEquals(1, _8bit_1.GetValue());
    }

    @Test
    public void testFromRawByteMixed() throws Exception {
        row4.SetFromRawByte(255);
        Assert.assertEquals(3, _2bit_1.GetValue());
        Assert.assertEquals(1, _1bit_9.GetValue());
        Assert.assertEquals(1, _1bit_10.GetValue());
        Assert.assertEquals(3, _2bit_2.GetValue());
        Assert.assertEquals(3, _2bit_3.GetValue());

        row4.SetFromRawByte(1);
        Assert.assertEquals(1, _2bit_1.GetValue());
        Assert.assertEquals(0, _1bit_9.GetValue());
        Assert.assertEquals(0, _1bit_10.GetValue());
        Assert.assertEquals(0, _2bit_2.GetValue());
        Assert.assertEquals(0, _2bit_3.GetValue());

        row4.SetFromRawByte(0);
        Assert.assertEquals(0, _2bit_1.GetValue());
        Assert.assertEquals(0, _1bit_9.GetValue());
        Assert.assertEquals(0, _1bit_10.GetValue());
        Assert.assertEquals(0, _2bit_2.GetValue());
        Assert.assertEquals(0, _2bit_3.GetValue());

        row4.SetFromRawByte(15);
        Assert.assertEquals(3, _2bit_1.GetValue());
        Assert.assertEquals(1, _1bit_9.GetValue());
        Assert.assertEquals(1, _1bit_10.GetValue());
        Assert.assertEquals(0, _2bit_2.GetValue());
        Assert.assertEquals(0, _2bit_3.GetValue());

        row4.SetFromRawByte(30);
        Assert.assertEquals(2, _2bit_1.GetValue());
        Assert.assertEquals(1, _1bit_9.GetValue());
        Assert.assertEquals(1, _1bit_10.GetValue());
        Assert.assertEquals(1, _2bit_2.GetValue());
        Assert.assertEquals(0, _2bit_3.GetValue());
    }

}