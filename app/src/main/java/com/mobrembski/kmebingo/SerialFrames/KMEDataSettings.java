package com.mobrembski.kmebingo.SerialFrames;

@SuppressWarnings("UnusedDeclaration")
public class KMEDataSettings extends KMEFrame {
    // region Variables declaration
    private SettingsRow[] rows = new SettingsRow[12];
    private SettingsItem SensorTypeOhm = new SettingsItem(1,7);
    private SettingsItem CutOffHighRPMEnabled = new SettingsItem(1,6);
    private SettingsItem CutOffEnabled = new SettingsItem(1,5);
    private SettingsItem TurnOnAtIncreasingRPM = new SettingsItem(1,4);
    private SettingsItem ATTEnabled = new SettingsItem(1,3);
    private SettingsItem PWAEnabled = new SettingsItem(1,2);
    private SettingsItem TemperatureSensorEnabled = new SettingsItem(1,1);
    private SettingsItem SensorTypeReserve = new SettingsItem(1,0);

    private SettingsItem LambdaType = new SettingsItem(4,4);
    private SettingsItem TPSType = new SettingsItem(4,0);

    private SettingsItem LambdaNeutralPoint = new SettingsItem(8,0);

    private SettingsItem LambdaDelay = new SettingsItem(8,0);

    private SettingsItem ButtonWithLevelIndicator = new SettingsItem(1,6);
    private SettingsItem EconMode = new SettingsItem(2,4);
    private SettingsItem StartOnLPG = new SettingsItem(1,3);
    private SettingsItem LowRPMSignalLevel = new SettingsItem(1,2);
    private SettingsItem LambdaEmulType = new SettingsItem(2,0);

    private SettingsItem LambdaEmulationHState = new SettingsItem(8,0);

    private SettingsItem LambdaEmulationLState = new SettingsItem(8,0);

    private SettingsItem StartOnGasOpenTime = new SettingsItem(6,0);

    private SettingsItem GasBenzineTime = new SettingsItem(6,0);

    private SettingsItem LPGOnTemperature = new SettingsItem(8,0);

    private SettingsItem TPSInertness = new SettingsItem(8,0);

    private LevelSensorType LevelSensor = LevelSensorType.Prog;
    private LambdaEmulationType LambdaEmulation = LambdaEmulationType.Course;
    private EconomyType EconomyMode = EconomyType.Normal;

    public enum LevelSensorType {
        Reserve,
        Ohm,
        Prog
    }

    public enum LambdaEmulationType {
        Course,
        Ground,
        Disconnected
    }

    public enum EconomyType {
        Normal,
        Eco,
        Sport
    }

    // endregion

    public KMEDataSettings() {
        super.askFrame = new byte[]{0x65, 0x03, 0x03, 0x6B};
        super.answerSize = 13;
        rows[0]= SettingsRow.makeSettingsRow(new SettingsItem[]{SensorTypeReserve, TemperatureSensorEnabled, PWAEnabled,
                ATTEnabled, TurnOnAtIncreasingRPM, CutOffEnabled, CutOffHighRPMEnabled, SensorTypeOhm});
        rows[1]= SettingsRow.makeSettingsRow(new SettingsItem[]{TPSType, LambdaType});
        rows[2]= SettingsRow.makeSettingsRow(new SettingsItem[]{LambdaNeutralPoint});
        rows[3]= SettingsRow.makeSettingsRow(new SettingsItem[]{LambdaDelay});
        rows[4]= SettingsRow.makeSettingsRow(new SettingsItem[]{LambdaEmulType, LowRPMSignalLevel, StartOnLPG, EconMode,
                ButtonWithLevelIndicator, new SettingsItem(1,7)});
        rows[5]= SettingsRow.makeSettingsRow(new SettingsItem[]{LambdaEmulationHState});
        rows[6]= SettingsRow.makeSettingsRow(new SettingsItem[]{LambdaEmulationLState});
        rows[7]= SettingsRow.makeSettingsRow(new SettingsItem[]{StartOnGasOpenTime});
        rows[8]= SettingsRow.makeSettingsRow(new SettingsItem[]{new SettingsItem(8,0)});
        rows[9]= SettingsRow.makeSettingsRow(new SettingsItem[]{GasBenzineTime});
        rows[10]= SettingsRow.makeSettingsRow(new SettingsItem[]{LPGOnTemperature});
        rows[11]= SettingsRow.makeSettingsRow(new SettingsItem[]{TPSInertness});
    }

    public KMEDataSettings(int[] array) {
        this();
        if (array == null || array.length == 0)
            return;

        for (int i=1;i<answerSize;i++)
            rows[i - 1].SetFromRawByte(array[i]);

        if (SensorTypeReserve.GetValueBool() && !SensorTypeOhm.GetValueBool())
            LevelSensor = LevelSensorType.Reserve;
        else if (!SensorTypeReserve.GetValueBool() && SensorTypeOhm.GetValueBool())
            LevelSensor = LevelSensorType.Ohm;
        else
            LevelSensor = LevelSensorType.Prog;

        switch (LambdaEmulType.GetValue()) {
            case 0:
                LambdaEmulation = LambdaEmulationType.Course;
                break;
            case 1:
                LambdaEmulation = LambdaEmulationType.Ground;
                break;
            case 2:
                LambdaEmulation = LambdaEmulationType.Disconnected;
                break;
        }

        switch (EconMode.GetValue()) {
            case 0:
                EconomyMode = EconomyType.Normal;
                break;
            case 1:
                EconomyMode = EconomyType.Eco;
                break;
            case 2:
                EconomyMode = EconomyType.Sport;
                break;
        }

        // TODO: Figure out how Neutral Point is coded
    }

    public LevelSensorType getLevelSensor() {
        return LevelSensor;
    }

    public int getLevelSensorRaw() {
        return rows[0].GenerateRawByte();
    }

    public void setLevelSensor(LevelSensorType levelSensor) {
        LevelSensor = levelSensor;
        switch(levelSensor) {
            case Reserve:
                SensorTypeOhm.SetValue(0);
                SensorTypeReserve.SetValue(1);
                break;
            case Ohm:
                SensorTypeOhm.SetValue(1);
                SensorTypeReserve.SetValue(0);
                break;
            case Prog:
                SensorTypeOhm.SetValue(0);
                SensorTypeReserve.SetValue(0);
                break;
        }
    }

    public LambdaEmulationType getLambdaEmulationType() {
        return LambdaEmulation;
    }

    public int getLambdaEmulationTypeRaw() {
        return rows[4].GenerateRawByte();
    }

    public void setLambdaEmulationType(LambdaEmulationType lambdaEmulationType) {
        LambdaEmulation = lambdaEmulationType;
        switch(lambdaEmulationType) {
            case Course:
                LambdaEmulType.SetValue(0);
                break;
            case Ground:
                LambdaEmulType.SetValue(1);
                break;
            case Disconnected:
                LambdaEmulType.SetValue(2);
                break;
        }
    }

    public EconomyType getEconomyMode() {
        return EconomyMode;
    }

    public int getEconomyModeRaw() {
        return EconMode.GenerateRawByte();
    }

    public void setEconomyMode(EconomyType economyMode) {
        EconomyMode = economyMode;
        switch(economyMode) {
            case Normal:
                EconMode.SetValue(0);
                break;
            case Eco:
                EconMode.SetValue(1);
                break;
            case Sport:
                EconMode.SetValue(2);
                break;
        }
    }

    public boolean getCutOffHighRPMEnabled() {
        return CutOffHighRPMEnabled.GetValueBool();
    }
    public int getCutOffHighRPMEnabledRaw() {
        return CutOffHighRPMEnabled.GenerateRawByte();
    }

    public void setCutOffHighRPMEnabled(boolean val) {
        CutOffHighRPMEnabled.SetValue(val);
    }

    public boolean getCutOffEnabled() {
        return CutOffEnabled.GetValueBool();
    }
    public int getCutOffEnabledRaw() {
        return CutOffEnabled.GenerateRawByte();
    }

    public void setCutOffEnabled(boolean val) {
        CutOffEnabled.SetValue(val);
    }

    public boolean getTurnOnAtIncreasingRPM() {
        return TurnOnAtIncreasingRPM.GetValueBool();
    }
    public int getTurnOnAtIncreasingRPMRaw() {
        return TurnOnAtIncreasingRPM.GenerateRawByte();
    }

    public void setTurnOnAtIncreasingRPM(boolean val) {
        TurnOnAtIncreasingRPM.SetValue(val);
    }

    public boolean getATTEnabled() {
        return ATTEnabled.GetValueBool();
    }
    public int getATTEnabledRaw() {
        return ATTEnabled.GenerateRawByte();
    }

    public void setATTEnabled(boolean val) {
        ATTEnabled.SetValue(val);
    }

    public boolean getPWAEnabled() {
        return !PWAEnabled.GetValueBool();
    }
    public int getPWAEnabledRaw() {
        return PWAEnabled.GenerateRawByte();
    }

    public void setPWAEnabled(boolean val) {
        PWAEnabled.SetValue(!val);
    }

    public boolean getTemperatureSensorEnabled() {
        return TemperatureSensorEnabled.GetValueBool();
    }

    public int getTemperatureSensorEnabledRaw() {
        return TemperatureSensorEnabled.GenerateRawByte();
    }

    public void setTemperatureSensorEnabled(boolean val) {
        TemperatureSensorEnabled.SetValue(val);
    }

    public int getLambdaType() {
        return LambdaType.GetValue();
    }

    public int getLambdaTypeRaw() {
        return LambdaType.GenerateRawByte();
    }

    public void setLambdaType(int val) {
        LambdaType.SetValue(val);
    }

    public int getTPSType() {
        return TPSType.GetValue();
    }

    public int getTPSTypeRaw() {
        return TPSType.GenerateRawByte();
    }

    public void setTPSType(int val) {
        TPSType.SetValue(val);
    }

    public int getLambdaNeutralPoint() {
        return LambdaNeutralPoint.GetValue();
    }

    public int getLambdaNeutralPointRaw() {
        return LambdaNeutralPoint.GenerateRawByte();
    }

    public void setLambdaNeutralPoint(int val) {
        LambdaNeutralPoint.SetValue(val);
    }

    public int getLambdaDelay() {
        return LambdaDelay.GetValue();
    }

    public int getLambdaDelayRaw() {
        return LambdaDelay.GenerateRawByte();
    }

    public void setLambdaDelay(int val) {
        LambdaDelay.SetValue(val);
    }

    public int getTPSInertness() {
        return TPSInertness.GetValue();
    }

    public int getTPSInertnessRaw() {
        return TPSInertness.GenerateRawByte();
    }

    public void setTPSInertness(int val) {
        this.TPSInertness.SetValue(val);
    }

    public int getGasBenzineTime() {
        return GasBenzineTime.GetValue();
    }

    public int getGasBenzineTimeRaw() {
        return GasBenzineTime.GenerateRawByte();
    }

    public void setGasBenzineTime(int val) {
        GasBenzineTime.SetValue(val);
    }

    public int getLPGOnTemperature() {
        return LPGOnTemperature.GetValue();
    }

    public int getLPHOnTemperatureRaw() {
        return LPGOnTemperature.GenerateRawByte();
    }

    public void setLPGOnTemperature(int val) {
        LPGOnTemperature.SetValue(val);
    }

    public int getStartOnGasOpenTime() {
        return StartOnGasOpenTime.GetValue();
    }

    public int getStartOnGasOpenTimeRaw() {
        return StartOnGasOpenTime.GenerateRawByte();
    }

    public void setStartOnGasOpenTime(int val) {
        StartOnGasOpenTime.SetValue(val);
    }

    public int getLambdaEmulationHState() {
        return LambdaEmulationHState.GetValue();
    }

    public int getLambdaEmulationHStateRaw() {
        return LambdaEmulationHState.GenerateRawByte();
    }

    public void setLambdaEmulationHState(int val) {
        LambdaEmulationHState.SetValue(val);
    }

    public int getLambdaEmulationLState() {
        return LambdaEmulationLState.GetValue();
    }

    public int getLambdaEmulationLStateRaw() {
        return LambdaEmulationLState.GenerateRawByte();
    }

    public void setLambdaEmulationLState(int val) {
        LambdaEmulationLState.SetValue(val);
    }

    public boolean getLowRPMSignalLevel() {
        return LowRPMSignalLevel.GetValueBool();
    }

    public int getLowRPMSingalLevelRaw() {
        return LowRPMSignalLevel.GenerateRawByte();
    }

    public void setLowRPMSignalLevel(boolean val) {
        LowRPMSignalLevel.SetValue(val);
    }

    public boolean getStartOnLPG() {
        return StartOnLPG.GetValueBool();
    }

    public int getStartOnLPGRaw() {
        return StartOnLPG.GenerateRawByte();
    }

    public void setStartOnLPG(boolean val) {
        StartOnLPG.SetValue(val);
    }

    public boolean getButtonWithLevelIndicator() {
        return ButtonWithLevelIndicator.GetValueBool();
    }

    public int getButtonWithLevelIndicatorRaw() {
        return ButtonWithLevelIndicator.GenerateRawByte();
    }

    public void setButtonWithLevelIndicator(boolean val) {
        ButtonWithLevelIndicator.SetValue(val);
    }


}
