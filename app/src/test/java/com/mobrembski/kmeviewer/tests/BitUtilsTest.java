package com.mobrembski.kmebingo.tests;

import com.mobrembski.kmebingo.v2.BitUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitUtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testBitIsSet() throws Exception {

    }

    @Test
    public void testSetBit() throws Exception {

    }

    @Test
    public void testGetMaskedBytes() throws Exception {

    }

    @Test
    public void testGetVoltage() throws Exception {

    }

    @Test
    public void testGetTemperature() throws Exception {

    }

    @Test
    public void testGetRPM() throws Exception {
        int tmp[] = BitUtils.GetRPMToRaw(1000);
        int tmp2 = BitUtils.GetRPMFromRaw(tmp[0] << 8 | tmp[1]);
        assertEquals(1000,tmp2);

        tmp = BitUtils.GetRPMToRaw(3400);
        tmp2 = BitUtils.GetRPMFromRaw(tmp[0] << 8 | tmp[1]);
        assertEquals(3400,tmp2);

        tmp = BitUtils.GetRPMToRaw(4500);
        tmp2 = BitUtils.GetRPMFromRaw(tmp[0] << 8 | tmp[1]);
        assertEquals(4500,tmp2);

        tmp = BitUtils.GetRPMToRaw(6000);
        tmp2 = BitUtils.GetRPMFromRaw(tmp[0] << 8 | tmp[1]);
        assertEquals(6000,tmp2);
    }

    @Test
    public void testGetRawDate() throws Exception {

    }

    @Test
    public void testGetRawRunningCounter() throws Exception {

    }

    @Test
    public void testPackFrame() throws Exception {

    }

    @Test
    public void testPackFrame1() throws Exception {

    }

    @Test
    public void testPowerOf2() throws Exception {
        assertEquals(2, BitUtils.PowerOf2(1));
        assertEquals(4, BitUtils.PowerOf2(2));
        assertEquals(8, BitUtils.PowerOf2(3));
        assertEquals(16, BitUtils.PowerOf2(4));
        assertEquals(32, BitUtils.PowerOf2(5));
        assertEquals(64, BitUtils.PowerOf2(6));
        assertEquals(128, BitUtils.PowerOf2(7));
    }
}