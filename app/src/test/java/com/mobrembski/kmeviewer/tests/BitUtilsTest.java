package com.mobrembski.kmeviewer.tests;

import com.mobrembski.kmeviewer.BitUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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