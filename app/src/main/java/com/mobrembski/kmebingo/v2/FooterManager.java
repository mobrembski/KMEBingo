package com.mobrembski.kmebingo.v2;

import android.app.Activity;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mobrembski.kmebingo.v2.bluetoothmanager.ISerialConnectionManager;
import com.mobrembski.kmebingo.v2.bluetoothmanager.SerialConnectionStatusEvent;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by byku on 18.06.17.
 */

public class FooterManager {

    public enum FooterDisplayMode {
        PACKETS, TIME;

        private static FooterDisplayMode[] vals = values();
        public FooterDisplayMode nextMode()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    private final TextSwitcher connStatusText;
    private final Activity activity;
    FooterDisplayMode currentMode = FooterDisplayMode.PACKETS;
    private final TextView packetCountLabel, errorsCountLabel, footerPrompt, dividerLabel;
    ScheduledExecutorService packetsInfoSchedule;
    ISerialConnectionManager btManager;
    private SerialConnectionStatusEvent.SerialConnectionStatus currentConnectionStatus;

    Runnable updateRunnable = new Runnable() {

        @Override
        public void run() {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (btManager == null) return;
                    switch(currentMode) {
                        case PACKETS:
                            showHideUnneededViews(false);
                            displayPackets();
                            break;
                        case TIME:
                            showHideUnneededViews(true);
                            displayTime();
                            break;
                    }
                    setConnectionStateText(btManager.getConnectionStatus());
                }
            });
        }
    };

    private void displayPackets() {
        footerPrompt.setText(R.string.packets);
        packetCountLabel.setText(String.valueOf(btManager.getTransmittedPacketCount()));
        errorsCountLabel.setText(String.valueOf(btManager.getErrorsCount()));
    }

    private void displayTime() {
        footerPrompt.setText(R.string.time);
        long timeConnected = btManager.getConnectedTime();
        packetCountLabel.setText(
                String.format(Locale.getDefault(), "%02dm:%02ds",
                        TimeUnit.MILLISECONDS.toMinutes(timeConnected),
                        TimeUnit.MILLISECONDS.toSeconds(timeConnected) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeConnected))
                ));
    }

    private void showHideUnneededViews(boolean hide) {
        if (hide) {
            errorsCountLabel.setVisibility(View.GONE);
            dividerLabel.setVisibility(View.GONE);
        } else {
            errorsCountLabel.setVisibility(View.VISIBLE);
            dividerLabel.setVisibility(View.VISIBLE);
        }
    }

    public FooterManager(Activity activity) {
        this.activity = activity;
        packetCountLabel = (TextView) activity.findViewById(R.id.packetCountLabel);
        errorsCountLabel = (TextView) activity.findViewById(R.id.errorsCountLabel);
        dividerLabel = (TextView) activity.findViewById(R.id.FooterDivider);
        footerPrompt = (TextView) activity.findViewById(R.id.FooterPrompt);
        connStatusText = (TextSwitcher) activity.findViewById(R.id.connectedLabel);
        setupConnectionStatusTextSwitcher();
    }

    public void setBTManager(ISerialConnectionManager btManager) {
        this.btManager = btManager;
        packetsInfoSchedule = Executors.newSingleThreadScheduledExecutor();
        packetsInfoSchedule.scheduleAtFixedRate(updateRunnable, 0, 250, TimeUnit.MILLISECONDS);
    }

    private void setupConnectionStatusTextSwitcher() {
        TextSwitcher connected = (TextSwitcher) activity.findViewById(R.id.connectedLabel);
        connected.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                AppCompatTextView connectionStatusText = new AppCompatTextView(activity);
                connectionStatusText.setGravity(Gravity.CENTER);
                connectionStatusText.setTextAppearance(
                        activity, android.R.style.TextAppearance_Large);
                connectionStatusText.setTextColor(
                        activity.getResources().getColor(android.R.color.holo_green_dark));
                return connectionStatusText;
            }
        });
        Animation in = AnimationUtils.loadAnimation(activity, android.R.anim.slide_in_left);
        connected.setInAnimation(in);
        Animation out = AnimationUtils.loadAnimation(activity, android.R.anim.slide_out_right);
        connected.setOutAnimation(out);
    }

    public void closeManager() {
        if (packetsInfoSchedule != null)
            packetsInfoSchedule.shutdownNow();
    }

    public void moveToNextDisplayMode() {
        currentMode = currentMode.nextMode();
    }

    public void setConnectionStateText(final SerialConnectionStatusEvent.SerialConnectionStatus status) {
        if(currentConnectionStatus == status) return;
        currentConnectionStatus = status;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED) {
                    connStatusText.setText(activity.getString(R.string.connected));
                }
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.DISCONNECTED) {
                    connStatusText.setText(activity.getString(R.string.disconnected));
                }
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTING) {
                    connStatusText.setText(activity.getString(R.string.connecting));
                }
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.ADAPTER_OFF) {
                    connStatusText.setText(activity.getString(R.string.bt_disabled));
                }
            }
        });
    }
}
