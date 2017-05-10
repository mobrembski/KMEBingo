package com.mobrembski.kmebingo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SensorLevelView extends LinearLayout {
    private final SeekBar valueBar;
    private final TextView valueText;
    private final ImageView valueImage;
    private boolean inTrackingTouch = false;
    private OnSeekbarValueChanged listener;

    public SensorLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.sensor_level_view, this);
        valueBar = (SeekBar) v.findViewById(R.id.LevelSeekBar);
        valueText = (TextView) v.findViewById(R.id.LevelTextView);
        valueImage = (ImageView) v.findViewById(R.id.LevelImageView);
        valueBar.setMax(255);
        final SensorLevelView levelView = this;
        valueBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                valueText.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                inTrackingTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (listener != null)
                    listener.callback(levelView);
                inTrackingTouch = false;
            }
        });
    }

    public int getSeekbarValue() {
        return valueBar.getProgress();
    }

    public void setSeekbarValue(int value) {
        // Don't update seekbar while user is interacting with him;
        if (!inTrackingTouch) {
            valueBar.setProgress(value);
            valueText.setText(String.valueOf(value));
        }
    }

    public ImageView getValueImage() {
        return valueImage;
    }

    public void setOnProgressChangedListener(OnSeekbarValueChanged changed) {
        listener = changed;
    }

    public interface OnSeekbarValueChanged {
        void callback(View v);
    }
}
