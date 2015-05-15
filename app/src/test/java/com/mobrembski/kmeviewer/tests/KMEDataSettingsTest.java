package com.mobrembski.kmebingo.tests;

import com.mobrembski.kmebingo.SerialFrames.KMEDataSettings;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.Exception;

public class KMEDataSettingsTest {

    private int inputTab[];
    private KMEDataSettings ds;
    private int tmp;

    @Before
    public void setUp() throws Exception {
        inputTab = new int[] {0x65, 0x3A, 0x12, 0x55, 0x55, 0x2A,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);
    }

    @Test
    public void testCorrectParseEnums() throws Exception {
        // TODO: Done frome memory, need to be verified.
        Assert.assertEquals(KMEDataSettings.LevelSensorType.Prog, ds.getLevelSensor());
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Disconnected, ds.getLambdaEmulationType());
        Assert.assertEquals(KMEDataSettings.EconomyType.Sport, ds.getEconomyMode());
    }

    @Test
    public void testCorrectSetLevelSensor() {
        Assert.assertEquals(KMEDataSettings.LevelSensorType.Prog, ds.getLevelSensor());
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0x3A, tmp);

        ds.setLevelSensor(KMEDataSettings.LevelSensorType.Reserve);
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0x3B, tmp);

        ds.setLevelSensor(KMEDataSettings.LevelSensorType.Ohm);
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0xBA, tmp);

        inputTab = new int[] {0x65, 0x6C, 0x12, 0x55, 0x55, 0x2A,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        Assert.assertEquals(KMEDataSettings.LevelSensorType.Prog, ds.getLevelSensor());
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0x6C, tmp);

        ds.setLevelSensor(KMEDataSettings.LevelSensorType.Reserve);
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0x6D, tmp);

        ds.setLevelSensor(KMEDataSettings.LevelSensorType.Ohm);
        tmp = ds.getLevelSensorRaw();
        Assert.assertEquals(0xEC, tmp);
    }

    @Test
    public void testCorrectSetEconomy() {
        Assert.assertEquals(KMEDataSettings.EconomyType.Sport, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0x2A, tmp);

        ds.setEconomyMode(KMEDataSettings.EconomyType.Normal);
        Assert.assertEquals(KMEDataSettings.EconomyType.Normal, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xA, tmp);

        ds.setEconomyMode(KMEDataSettings.EconomyType.Eco);
        Assert.assertEquals(KMEDataSettings.EconomyType.Eco, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0x1A, tmp);

        ds.setEconomyMode(KMEDataSettings.EconomyType.Sport);
        Assert.assertEquals(KMEDataSettings.EconomyType.Sport, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0x2A, tmp);

        inputTab = new int[] {0x65, 0x6C, 0x12, 0x55, 0x55, 0xF8,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        // Don't know what could means when both economy mode bits are set
        Assert.assertEquals(null, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xF8, tmp);

        ds.setEconomyMode(KMEDataSettings.EconomyType.Normal);
        Assert.assertEquals(KMEDataSettings.EconomyType.Normal, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xC8, tmp);

        ds.setEconomyMode(KMEDataSettings.EconomyType.Sport);
        Assert.assertEquals(KMEDataSettings.EconomyType.Sport, ds.getEconomyMode());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xE8, tmp);
    }

    @Test
    public void testCorrectSetLambdaEmulationType() {
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Disconnected, ds.getLambdaEmulationType());
        tmp = ds.getLambdaEmulationTypeRaw();
        Assert.assertEquals(0x2A, tmp);

        ds.setLambdaEmulationType(KMEDataSettings.LambdaEmulationType.Ground);
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Ground, ds.getLambdaEmulationType());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0x29, tmp);

        ds.setLambdaEmulationType(KMEDataSettings.LambdaEmulationType.Course);
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Course, ds.getLambdaEmulationType());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0x28, tmp);

        inputTab = new int[] {0x65, 0x6C, 0x12, 0x55, 0x55, 0xF8,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Course, ds.getLambdaEmulationType());
        tmp = ds.getLambdaEmulationTypeRaw();
        Assert.assertEquals(0xF8, tmp);

        ds.setLambdaEmulationType(KMEDataSettings.LambdaEmulationType.Ground);
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Ground, ds.getLambdaEmulationType());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xF9, tmp);

        ds.setLambdaEmulationType(KMEDataSettings.LambdaEmulationType.Disconnected);
        Assert.assertEquals(KMEDataSettings.LambdaEmulationType.Disconnected, ds.getLambdaEmulationType());
        tmp = ds.getEconomyModeRaw();
        Assert.assertEquals(0xFA, tmp);
    }

    @Test
    public void testCorrectSetCutOff() {
        Assert.assertEquals(true, ds.getCutOffEnabled());
        tmp = ds.getCutOffEnabledRaw();
        Assert.assertEquals(0x3A, tmp);

        ds.setCutOffEnabled(false);
        Assert.assertEquals(false, ds.getCutOffEnabled());
        tmp = ds.getCutOffEnabledRaw();
        Assert.assertEquals(0x1A, tmp);

        inputTab = new int[] {0x65, 0xAF, 0x12, 0x55, 0x55, 0x2A,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        Assert.assertEquals(true, ds.getCutOffEnabled());
        tmp = ds.getCutOffEnabledRaw();
        Assert.assertEquals(0xAF, tmp);

        ds.setCutOffEnabled(false);
        Assert.assertEquals(false, ds.getCutOffEnabled());
        tmp = ds.getCutOffEnabledRaw();
        Assert.assertEquals(0x8F, tmp);
    }

    @Test
    public void testCorrectSetLambdaType() {
        Assert.assertEquals(1, ds.getLambdaType());
        tmp = ds.getLambdaTypeRaw();
        Assert.assertEquals(0x12, tmp);

        ds.setLambdaType(14);
        Assert.assertEquals(14, ds.getLambdaType());
        tmp = ds.getLambdaTypeRaw();
        Assert.assertEquals(0xE2, tmp);

        inputTab = new int[] {0x65, 0xAF, 0x45, 0x55, 0x55, 0x2A,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        Assert.assertEquals(4, ds.getLambdaType());
        tmp = ds.getLambdaTypeRaw();
        Assert.assertEquals(0x45, tmp);

        ds.setLambdaType(10);
        Assert.assertEquals(10, ds.getLambdaType());
        tmp = ds.getLambdaTypeRaw();
        Assert.assertEquals(0xA5, tmp);
    }

    @Test
    public void testCorrectSetTPSType() {
        Assert.assertEquals(2, ds.getTPSType());
        tmp = ds.getTPSTypeRaw();
        Assert.assertEquals(0x12, tmp);

        ds.setTPSType(15);
        Assert.assertEquals(15, ds.getTPSType());
        tmp = ds.getTPSTypeRaw();
        Assert.assertEquals(0x1F, tmp);

        inputTab = new int[] {0x65, 0xAF, 0x45, 0x55, 0x55, 0x2A,
                0xFF, 0xFF, 0x28, 0x00, 0x38, 0x43, 0x12};
        ds = new KMEDataSettings(inputTab);

        Assert.assertEquals(5, ds.getTPSType());
        tmp = ds.getTPSTypeRaw();
        Assert.assertEquals(0x45, tmp);

        ds.setTPSType(10);
        Assert.assertEquals(10, ds.getTPSType());
        tmp = ds.getTPSTypeRaw();
        Assert.assertEquals(0x4A, tmp);
    }

    @Test
    public void testCorrectSetLambdaDelay() {
        Assert.assertEquals(85, ds.getLambdaDelay());
        tmp = ds.getLambdaDelayRaw();
        Assert.assertEquals(0x55, tmp);

        ds.setLambdaDelay(240);
        Assert.assertEquals(240, ds.getLambdaDelay());
        tmp = ds.getLambdaDelayRaw();
        Assert.assertEquals(0xF0, tmp);
    }

    @Test
    public void testCorrectSetButtonWithLevelIndicator() {
        Assert.assertEquals(false, ds.getButtonWithLevelIndicator());
        tmp = ds.getButtonWithLevelIndicatorRaw();
        Assert.assertEquals(0x2A, tmp);

        ds.setButtonWithLevelIndicator(true);
        Assert.assertEquals(true, ds.getButtonWithLevelIndicator());
        tmp = ds.getButtonWithLevelIndicatorRaw();
        Assert.assertEquals(0x6A, tmp);
    }

    @Test
    public void testMultipleSet() {
        Assert.assertEquals(false, ds.getButtonWithLevelIndicator());
        tmp = ds.getButtonWithLevelIndicatorRaw();
        Assert.assertEquals(0x2A, tmp);

        ds.setButtonWithLevelIndicator(true);
        ds.setLowRPMSignalLevel(true);
        tmp = ds.getLowRPMSingalLevelRaw();
        Assert.assertEquals(0x6E, tmp);
    }
}