package com.mobrembski.kmeviewer;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity implements Observer {
    private static final int REQUEST_DISCOVERY = 0x1;
    private BluetoothController btcntrl;
    private SharedPreferences prefs;
    private KMEViewerTab actualParametersFragment, kmeInfoFragment, settingsFragment;
    private ActionBar.Tab actualParamTab, infoTab, settingsTab;
    private String btAddress;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_DeviceSelect:
                if (btcntrl != null)
                    btcntrl.Stop();
                Intent intent = new Intent(this, DiscoveryActivity.class);
                Toast.makeText(this, "select device to connect", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_DISCOVERY);
                return true;
            case R.id.action_StayScreenOn:
                item.setChecked(!item.isChecked());
                getWindow().getDecorView().getRootView().setKeepScreenOn(item.isChecked());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        Time now = new Time();
        now.setToNow();
        final long diff = TimeUnit.MILLISECONDS.toSeconds(now.toMillis(true)-btcntrl.StartTime.toMillis(true));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView errors = (TextView) findViewById(R.id.errorsCountLabel);
                TextView packets = (TextView) findViewById(R.id.packetCountLabel);
                TextView connected = (TextView) findViewById(R.id.connectedLabel);
                if (btcntrl == null)
                    return;
                errors.setText(String.valueOf(btcntrl.GetErrorsCount()));
                packets.setText(String.valueOf(btcntrl.GetRecvPacketsCount()));
                if(!btcntrl.GetConnected())
                    connected.setText("Disconnected");
                else
                    connected.setText(DateUtils.formatElapsedTime(diff));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (btcntrl != null)
            btcntrl.Stop();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (btcntrl != null)
            btcntrl.Stop();
        btcntrl = null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (btcntrl == null)
            CreateAndStartBtController(btAddress);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_DISCOVERY) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device != null) {
            String newAddress = device.getAddress();
            CreateAndStartBtController(newAddress);
            prefs.edit().putString("com.mobrembski.kmeviewer.Device", newAddress).apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = this.getSharedPreferences("com.mobrembski.kmeviewer", Context.MODE_PRIVATE);
        btAddress = prefs.getString("com.mobrembski.kmeviewer.Device", "00:12:6F:2E:8A:03");
        CreateAndStartBtController(btAddress);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actualParametersFragment = new ActualParametersTab();
        kmeInfoFragment = new KmeInfoTab();
        settingsFragment = new KMESettingsTab();
        actualParamTab = actionBar.newTab();
        actualParamTab.setText("ActualParam");
        infoTab = actionBar.newTab();
        infoTab.setText("Info");
        settingsTab = actionBar.newTab();
        settingsTab.setText("Settings");
        actualParamTab.setTabListener(new TabListener(actualParametersFragment, btcntrl));
        infoTab.setTabListener(new TabListener(kmeInfoFragment, btcntrl));
        settingsTab.setTabListener(new TabListener(settingsFragment,btcntrl));
        actionBar.addTab(actualParamTab);
        actionBar.addTab(settingsTab);
        actionBar.addTab(infoTab);

    }

    private void CreateAndStartBtController(String address) {
        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        btcntrl = new BluetoothController(device);
        btcntrl.addObserver(this);
        btcntrl.Start();
    }
}
