package com.mobrembski.kmeviewer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
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

import com.mobrembski.kmeviewer.Tabs.ActualParametersTab;
import com.mobrembski.kmeviewer.Tabs.KMESettingsTab;
import com.mobrembski.kmeviewer.Tabs.KMEViewerTab;
import com.mobrembski.kmeviewer.Tabs.KmeInfoTab;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements Observer,
        ControllerExceptionEvent, ControllerEvent {
    private static final int REQUEST_DISCOVERY = 0x1;
    private static final int REQUEST_BT_ENABLE = 0x2;
    private BluetoothAdapter btAdapter;
    private SharedPreferences prefs;
    private String btAddress;
    private ProgressDialog connectProgressDialog;

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
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_DISCOVERY);
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
        final long diff = TimeUnit.MILLISECONDS.toSeconds(now.toMillis(true) - BluetoothController.getInstance().StartTime.toMillis(true));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView errors = (TextView) findViewById(R.id.errorsCountLabel);
                TextView packets = (TextView) findViewById(R.id.packetCountLabel);
                TextView connected = (TextView) findViewById(R.id.connectedLabel);
                if (errors != null)
                    errors.setText(String.valueOf(BluetoothController.getInstance().GetErrorsCount()));
                if (packets != null)
                    packets.setText(String.valueOf(BluetoothController.getInstance().GetRecvPacketsCount()));
                if (connected != null) {
                    if (!BluetoothController.getInstance().IsConnected())
                        connected.setText("Disconnected");
                    else
                        connected.setText(DateUtils.formatElapsedTime(diff));
                }
            }
        });
    }

    // TODO: fragments doesn't know about finishing app.
    @Override
    protected void onDestroy() {
        BluetoothController.getInstance().Disconnect();
        BluetoothController.getInstance().deleteObserver(this);
        // TabListener OnTabUnselected isn't called after pressing back key.
        // This is a override to remove observer and thus remove Tab object instance.
        BluetoothController.getInstance().RemoveAllListeners();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        BluetoothController.getInstance().Disconnect();
        BluetoothController.getInstance().deleteObserver(this);
        BluetoothController.getInstance().RemoveOnConnectionListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        CreateAndStartBtController(btAddress);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ActionBar ab = getActionBar();
        assert ab != null;
        outState.putInt("selected-tab", ab.getSelectedNavigationIndex());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(getApplicationContext(), "You must enable Bluetooth!", Toast.LENGTH_LONG).show();
                return;
            }
            return;
        }
        if (requestCode != REQUEST_DISCOVERY) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        final BluetoothDevice device = data.getParcelableExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        if (device != null) {
            btAddress = device.getAddress();
            prefs.edit().putString("com.mobrembski.kmeviewer.Device", btAddress).apply();
            BluetoothController.getInstance().SetDevice(device);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckIfBtAdapterExist();
        connectProgressDialog = new ProgressDialog(this);
        connectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        connectProgressDialog.setMessage("Connecting...");
        int selectedTab = 0;
        if (savedInstanceState != null)
            selectedTab = savedInstanceState.getInt("selected-tab");
        prefs = this.getSharedPreferences("com.mobrembski.kmeviewer", Context.MODE_PRIVATE);
        btAddress = prefs.getString("com.mobrembski.kmeviewer.Device", "00:12:6F:2E:8A:03");
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        KMEViewerTab actualParametersFragment = new ActualParametersTab();
        KMEViewerTab kmeInfoFragment = new KmeInfoTab();
        KMEViewerTab settingsFragment = new KMESettingsTab();
        ActionBar.Tab actualParamTab = actionBar.newTab();
        actualParamTab.setText("ActualParam");
        actualParamTab.setTabListener(new TabListener(actualParametersFragment));
        ActionBar.Tab infoTab = actionBar.newTab();
        infoTab.setText("Info");
        infoTab.setTabListener(new TabListener(kmeInfoFragment));
        ActionBar.Tab settingsTab = actionBar.newTab();
        settingsTab.setText("Settings");
        settingsTab.setTabListener(new TabListener(settingsFragment));
        actionBar.addTab(actualParamTab);
        actionBar.addTab(settingsTab);
        actionBar.addTab(infoTab);
        actionBar.setSelectedNavigationItem(selectedTab);
    }

    private void CreateAndStartBtController(String address) {
        // Device doesn't have a Bluetooth at all.
        if (btAdapter == null)
            return;
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            return;
        }
        connectProgressDialog.show();
        final BluetoothDevice device = btAdapter.getRemoteDevice(address);
        if (BluetoothController.getInstance().IsConnected())
            BluetoothController.getInstance().Disconnect();
        BluetoothController.getInstance().SetDevice(device);
        BluetoothController.getInstance().Connect(this);
        BluetoothController.getInstance().addObserver(this);
        BluetoothController.getInstance().AddOnConnectionListener(this);
    }

    private void CheckIfBtAdapterExist() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle("No BT Adapter");
            alertDialogBuilder.setMessage("We're sorry, but no bluetooth adapter has been found.\n" +
                    "However, if your device support USB OTG, you can try connecting" +
                    "USB Bluetooth adapter and try again.");
            alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dial = alertDialogBuilder.create();
            dial.show();
        }
    }

    @Override
    public void onConnectionException() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(connectProgressDialog.isShowing())
                    connectProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Cannot connect to device!", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onConnectionStopping() {

    }

    @Override
    public void onConnectionStarting() {
        if(connectProgressDialog.isShowing())
            connectProgressDialog.dismiss();
    }
}
