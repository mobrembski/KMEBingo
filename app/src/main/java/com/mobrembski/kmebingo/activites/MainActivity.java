package com.mobrembski.kmebingo.activites;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.Tabs.ActualParametersTab;
import com.mobrembski.kmebingo.Tabs.KMEInfoTab;
import com.mobrembski.kmebingo.Tabs.SettingsTab.KMESettingsTab;
import com.mobrembski.kmebingo.bluetoothmanager.BluetoothConnectionManager;
import com.mobrembski.kmebingo.bluetoothmanager.SerialConnectionStatusEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_DISCOVERY = 0x1;
    private static final int REQUEST_BT_ENABLE = 0x2;
    private BluetoothAdapter btAdapter;
    private SharedPreferences prefs;
    private String btAddress;
    private ProgressDialog connectProgressDialog;
    private ActionBar.Tab actualParamTab;
    private ActionBar.Tab settingsTab;
    private ActionBar.Tab infoTab;
    // TODO Fix this dep
    public BluetoothConnectionManager btManager;
    ScheduledExecutorService packetsInfoSchedule;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckIfBtAdapterExist();
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        prefs = this.getSharedPreferences("com.mobrembski.kmebingo", Context.MODE_PRIVATE);
//        connectProgressDialog = new ProgressDialog(this);
//        connectProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        connectProgressDialog.setMessage("Connecting...");
//        int selectedTab = 0;
//        if (savedInstanceState != null)
//            selectedTab = savedInstanceState.getInt("selected-tab");
//        ActionBar actionBar = getActionBar();
//        assert actionBar != null;
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        KMEViewerTab actualParametersFragment = new ActualParametersTab();
//        KMEViewerTab kmeInfoFragment = new KMEInfoTab();
//        KMEViewerTab settingsFragment = new KMESettingsTab();
//        actualParamTab = actionBar.newTab();
//        infoTab = actionBar.newTab();
//        settingsTab = actionBar.newTab();
//        prepareActionBarTitles();
//        actualParamTab.setTabListener(new TabListener(actualParametersFragment));
//        actualParamTab.setIcon(R.drawable.actual_params_24x24);
//        infoTab.setTabListener(new TabListener(kmeInfoFragment));
//        infoTab.setIcon(R.drawable.info_24x24);
//        settingsTab.setTabListener(new TabListener(settingsFragment));
//        settingsTab.setIcon(R.drawable.settings_24x24);
//        actionBar.addTab(actualParamTab);
//        actionBar.addTab(settingsTab);
//        actionBar.addTab(infoTab);
//        actionBar.setSelectedNavigationItem(selectedTab);
//        prefs = this.getSharedPreferences("com.mobrembski.kmebingo", Context.MODE_PRIVATE);
//        btAddress = prefs.getString("com.mobrembski.kmebingo.Device", "NULL");
//        if (btAddress.equals("NULL")) {
//            if (btAdapter != null && btAdapter.isEnabled()) {
//                Intent serverIntent = new Intent(this, DeviceListActivity.class);
//                startActivityForResult(serverIntent, REQUEST_DISCOVERY);
//            }
//        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ActualParametersTab(), "");
        adapter.addFragment(new KMESettingsTab(), "");
        adapter.addFragment(new KMEInfoTab(), "");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.actual_params_24x24);
        tabLayout.getTabAt(1).setIcon(R.drawable.settings_24x24);
        tabLayout.getTabAt(2).setIcon(R.drawable.info_24x24);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onPause() {
        if (btManager != null) btManager.stopConnections();
        EventBus.getDefault().unregister(this);
        packetsInfoSchedule.shutdownNow();
        super.onPause();
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        btAddress = prefs.getString("com.mobrembski.kmebingo.Device", "NULL");
        if (!btAddress.equals("NULL")) {
            btManager = new BluetoothConnectionManager(btAddress);
            btManager.startConnecting();
        } else {
            if (btAdapter != null && btAdapter.isEnabled()) {
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_DISCOVERY);
            }
        }
        packetsInfoSchedule = Executors.newSingleThreadScheduledExecutor();
        packetsInfoSchedule.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (btManager == null) return;
                        TextView packets = (TextView) findViewById(R.id.packetCountLabel);
                        if (packets != null)
                            packets.setText(String.valueOf(btManager.getTransmittedPacketCount()));
                        packets = (TextView) findViewById(R.id.errorsCountLabel);
                        if (packets != null)
                            packets.setText(String.valueOf(btManager.getErrorsCount()));
                        setConnectionStateText(btManager.getConnectionStatus());
                    }
                });
            }
        }, 0, 250, TimeUnit.MILLISECONDS);
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        prepareActionBarTitles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Workaround for not showed icons on Android 3.0+
    // http://stackoverflow.com/questions/19750635/icon-in-menu-not-showing-in-android
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_DeviceSelect:
                CheckIfBtAdapterExist();
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_DISCOVERY);
                return true;
            case R.id.action_StayScreenOn:
                item.setChecked(!item.isChecked());
                getWindow().getDecorView().getRootView().setKeepScreenOn(item.isChecked());
                return true;
            case R.id.action_About:
                AboutDialog about = new AboutDialog(this);
                about.setTitle("About...");
                about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onEvent(final SerialConnectionStatusEvent config) {
        setConnectionStateText(config.currentStatus);
    }

    private void setConnectionStateText(final SerialConnectionStatusEvent.SerialConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView connected = (TextView) findViewById(R.id.connectedLabel);
                if (connected == null) return;
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTED) {
                    connected.setText("Connected");
                }
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.DISCONNECTED) {
                    connected.setText("Disconnected");
                }
                if (status == SerialConnectionStatusEvent.SerialConnectionStatus.CONNECTING) {
                    connected.setText("Connecting...");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == RESULT_CANCELED) {
                // FIXME: For some reason we've got RESULT_CANCELLED when user has defined address
                // and Bluetooth wasn't turned on. Can we get ENABLE_BLUETOOTH result twice?
                // Need to be checked.
                //Toast.makeText(getApplicationContext(), "You must enable Bluetooth!",
                //        Toast.LENGTH_LONG).show();
                return;
            }
            if (btAddress.equals("NULL")) {
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_DISCOVERY);
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
            prefs.edit().putString("com.mobrembski.kmebingo.Device", btAddress).apply();
            if (btManager != null) btManager.stopConnections();
            packetsInfoSchedule.shutdownNow();
        }
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
            return;
        }
        CheckIfBtAdapterIsEnabled();
    }

    private void CheckIfBtAdapterIsEnabled() {
        if (btAdapter == null)
            return;
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            return;
        }
    }

    private void prepareActionBarTitles() {
        int width = getResources().getConfiguration().screenWidthDp;
        /*if (width < 360 ) {
            actualParamTab.setText("");
            infoTab.setText("");
            settingsTab.setText("");
        }
        else {
            actualParamTab.setText("Readings");
            infoTab.setText("Info");
            settingsTab.setText("Settings");
        }*/
    }
}
