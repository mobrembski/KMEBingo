package com.mobrembski.kmeviewer.SerialFrames;

@SuppressWarnings("UnusedDeclaration")
public class KMEDataConfig extends KMEFrame {
    // region Variables declaration
    public SettingsRow[] rows = new SettingsRow[14];
    public SettingsItem ActuatorMaxOpenOnLoad = new SettingsItem(8, 0);
    public SettingsItem ActuatorMinOpenOnLoad = new SettingsItem(8, 0);
    public SettingsItem ActuatorMaxOpenOnIdle = new SettingsItem(8, 0);
    public SettingsItem ActuatorMinOpenOnIdle = new SettingsItem(8, 0);
    public SettingsItem ATTTPSSenseLevel = new SettingsItem(8, 0);
    public SettingsItem ATTEnrichFuelMixture = new SettingsItem(8, 0);
    public SettingsItem SwitchOnLPGRPM1 = new SettingsItem(8, 0);
    public SettingsItem SwitchOnLPGRPM2 = new SettingsItem(8, 0);
    public SettingsItem IgnitionType = new SettingsItem(5, 0);
    public SettingsItem MinimalCutoffRPMS1 = new SettingsItem(8, 0);
    public SettingsItem MinimalCutoffRPMS2 = new SettingsItem(8, 0);
    public SettingsItem CutoffMixtureImpoverishment = new SettingsItem(7, 0);
    public SettingsItem HighRPMLimit1 = new SettingsItem(8, 0);
    public SettingsItem HighRPMLimit2 = new SettingsItem(8, 0);
    // endregion

    public KMEDataConfig() {
        super.askFrame = new byte[]{0x65, 0x04, 0x04, 0x6D};
        super.answerSize = 15;
        rows[0]= SettingsRow.makeSettingsRow(new SettingsItem[]{ActuatorMaxOpenOnLoad});
        rows[1]= SettingsRow.makeSettingsRow(new SettingsItem[]{ActuatorMinOpenOnLoad});
        rows[2]= SettingsRow.makeSettingsRow(new SettingsItem[]{ATTTPSSenseLevel});
        rows[3]= SettingsRow.makeSettingsRow(new SettingsItem[]{ATTEnrichFuelMixture});
        rows[4]= SettingsRow.makeSettingsRow(new SettingsItem[]{SwitchOnLPGRPM1});
        rows[5]= SettingsRow.makeSettingsRow(new SettingsItem[]{SwitchOnLPGRPM2});
        rows[6]= SettingsRow.makeSettingsRow(new SettingsItem[]{IgnitionType});
        rows[7]= SettingsRow.makeSettingsRow(new SettingsItem[]{MinimalCutoffRPMS1});
        rows[8]= SettingsRow.makeSettingsRow(new SettingsItem[]{MinimalCutoffRPMS2});
        rows[9]= SettingsRow.makeSettingsRow(new SettingsItem[]{CutoffMixtureImpoverishment});
        rows[10]= SettingsRow.makeSettingsRow(new SettingsItem[]{HighRPMLimit1});
        rows[11]= SettingsRow.makeSettingsRow(new SettingsItem[]{HighRPMLimit2});
        rows[12]= SettingsRow.makeSettingsRow(new SettingsItem[]{ActuatorMaxOpenOnIdle});
        rows[13]= SettingsRow.makeSettingsRow(new SettingsItem[]{ActuatorMinOpenOnIdle});
        HighRPMLimit1.SetValue(0xFF);
        HighRPMLimit2.SetValue(0xFF);
        SwitchOnLPGRPM1.SetValue(0xFF);
        SwitchOnLPGRPM2.SetValue(0xFF);
        MinimalCutoffRPMS1.SetValue(0xFF);
        MinimalCutoffRPMS2.SetValue(0xFF);
    }

    public KMEDataConfig(int[] array) {
        this();
        if (array == null || array.length == 0)
            return;

        for (int i = 1; i < answerSize; i++)
            rows[i - 1].SetFromRawByte(array[i]);

    }

}
