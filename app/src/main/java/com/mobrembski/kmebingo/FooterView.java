package com.mobrembski.kmebingo;

import android.content.Context;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mobrembski.kmebingo.bluetoothmanager.ISerialConnectionManager;
import com.mobrembski.kmebingo.bluetoothmanager.SerialConnectionStatusEvent;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by byku on 18.06.17.
 */

public class FooterView extends LinearLayout {

    private final Context ctx;
    private final TextSwitcher connStatusText;
    private SerialConnectionStatusEvent.SerialConnectionStatus currentConnectionStatus;
    private ScheduledExecutorService packetsInfoSchedule;
    private ISerialConnectionManager btManager;
    private long startTime;

    public FooterView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        ctx = context;
        startTime = SystemClock.currentThreadTimeMillis();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.footer, this);
        connStatusText = (TextSwitcher) v.findViewById(R.id.connectedLabel);
        setupConnectionStatusTextSwitcher();
        setConnectionStateText(SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED);
    }

    public void setConnectionStateText(final SerialConnectionStatusEvent.SerialConnectionStatus status) {
        if(currentConnectionStatus == status) return;
        currentConnectionStatus = status;
        if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED) {
            connStatusText.setText(ctx.getResources().getString(R.string.connected));
        }
        if (status == SerialConnectionStatusEvent.SerialConnectionStatus.DISCONNECTED) {
            connStatusText.setText(ctx.getResources().getString(R.string.disconnected));
        }
        if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTING) {
            connStatusText.setText(ctx.getResources().getString(R.string.connecting));
        }
        if (status == SerialConnectionStatusEvent.SerialConnectionStatus.ADAPTER_OFF) {
            connStatusText.setText(ctx.getResources().getString(R.string.bt_disabled));
        }
    }

    private void setupConnectionStatusTextSwitcher() {
        TextSwitcher connected = (TextSwitcher) findViewById(R.id.connectedLabel);
        connected.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                AppCompatTextView connectionStatusText = new AppCompatTextView(ctx);
                connectionStatusText.setGravity(Gravity.CENTER);
                connectionStatusText.setTextAppearance(
                        ctx, android.R.style.TextAppearance_Large);
                connectionStatusText.setTextColor(
                        getResources().getColor(android.R.color.holo_green_dark));
                return connectionStatusText;
            }
        });
        Animation in = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_in_left);
        connected.setInAnimation(in);
        Animation out = AnimationUtils.loadAnimation(ctx, android.R.anim.slide_out_right);
        connected.setOutAnimation(out);
    }

    public void updatePacketLabels(int transmittedCount, int errorsCount) {
        TextView packets = (TextView) findViewById(R.id.packetCountLabel);
        packets.setText(String.valueOf(transmittedCount));
        packets = (TextView) findViewById(R.id.errorsCountLabel);
        packets.setText(String.valueOf(errorsCount));
    }

    private static String getElapsedTimeMinutesSecondsString(long miliseconds) {
        long elapsedTime = miliseconds;
        String format = String.format("%%0%dd", 2);
        elapsedTime = elapsedTime / 1000;
        String seconds = String.format(format, elapsedTime % 60);
        String minutes = String.format(format, elapsedTime / 60);
        return minutes + ":" + seconds;
    }
}
