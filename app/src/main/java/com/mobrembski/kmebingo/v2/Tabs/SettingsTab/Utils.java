package com.mobrembski.kmebingo.v2.Tabs.SettingsTab;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

class Utils {
     static void setSpinnerSelectionWithoutCallingListener(final Spinner spinner, final int selection) {
        final AdapterView.OnItemSelectedListener l = spinner.getOnItemSelectedListener();
        spinner.setOnItemSelectedListener(null);
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(selection, false);
                spinner.setTag(selection);
                spinner.post(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setOnItemSelectedListener(l);
                    }
                });
            }
        });
    }

    static ArrayAdapter<String> createActuatorStepsArrayAdapter(View parent) {
        return createActuatorStepsArrayAdapter("", parent);
    }

    static ArrayAdapter<String> createActuatorStepsArrayAdapter(String prefix, View parent) {
        List<String> actuatorStepsStrings = new ArrayList<>();
        for(int i=0; i<=255; i++)
            actuatorStepsStrings.add(prefix + String.valueOf(i));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.getContext(),
                android.R.layout.simple_spinner_item, actuatorStepsStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    static ArrayAdapter<String> createActuatorSpeedStepsAdapter(int startpoint, View parent) {
        List<String> actuatorStepsStrings = new ArrayList<>();
        for(int i=0; i<=15; i++) {
            int steps = startpoint - i*2;
            if (steps <0)
                actuatorStepsStrings.add(String.valueOf(steps));
            else
                actuatorStepsStrings.add("+" + String.valueOf(steps));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                parent.getContext(),
                android.R.layout.simple_spinner_item, actuatorStepsStrings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }
}
