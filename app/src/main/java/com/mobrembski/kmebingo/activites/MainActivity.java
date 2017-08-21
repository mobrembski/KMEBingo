package com.mobrembski.kmebingo.activites;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mobrembski.kmebingo.FooterManager;
import com.mobrembski.kmebingo.R;
import com.mobrembski.kmebingo.Tabs.ActualParametersTab;
import com.mobrembski.kmebingo.Tabs.KMEInfoTab;
import com.mobrembski.kmebingo.Tabs.KMEViewerTab;
import com.mobrembski.kmebingo.Tabs.SettingsTab.KMESettingsTab;
import com.mobrembski.kmebingo.bluetoothmanager.BluetoothConnectionManager;
import com.mobrembski.kmebingo.bluetoothmanager.ISerialConnectionManager;
import com.mobrembski.kmebingo.bluetoothmanager.NullConnectionManager;
import com.mobrembski.kmebingo.bluetoothmanager.SerialConnectionStatusEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DeviceListDialog.onDeviceSelectedInterface {
    private static final boolean DEMO_MODE = false;
    private static final int REQUEST_DISCOVERY = 0x1;
    private static final int REQUEST_BT_ENABLE = 0x2;
    private static final String PACKAGE_NAME = "com.mobrembski.kmebingo";
    private static final String DARK_MODE_PREF_NAME = PACKAGE_NAME + ".DarkMode";
    private static final String DEVICE_PREF_NAME = PACKAGE_NAME + ".Device";
    private static final String STAY_ON_KEY_NAME = "StayScreenOn";

    private BluetoothAdapter btAdapter;
    private SharedPreferences prefs;
    private String btAddress;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdapter viewPagerAdapter;
    private TextView footerPrompt;
    private boolean darkMode;
    private boolean stayScreenOnEnabled = false;
    private boolean doBTEnabledCheck = true;
    // TODO Fix this dep
    public ISerialConnectionManager btManager;
    private FooterManager footerDisplayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = this.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        setupDarkMode(prefs);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        setupKeepingScreenOn(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetsAbsolute(0,0);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        footerDisplayManager = new FooterManager(this);
        footerPrompt = (TextView) findViewById(R.id.FooterPrompt);
        footerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                footerDisplayManager.moveToNextDisplayMode();
            }
        });
    }

    private void setupDarkMode(SharedPreferences prefs) {
        darkMode = prefs.getBoolean(DARK_MODE_PREF_NAME, true);
        if (darkMode) {
            setTheme(R.style.MyMaterialThemeDark);
        }
    }

    private void setupKeepingScreenOn(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            stayScreenOnEnabled = savedInstanceState.getBoolean(STAY_ON_KEY_NAME, false);
        }
        setStayScreenOn(stayScreenOnEnabled);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ActualParametersTab(), "");
        viewPagerAdapter.addFragment(new KMESettingsTab(), "");
        viewPagerAdapter.addFragment(new KMEInfoTab(), "");
        viewPager.setAdapter(viewPagerAdapter);
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
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (DEMO_MODE) {
            openDemoManager();
        } else {
            btAddress = prefs.getString(DEVICE_PREF_NAME, "NULL");
            if (btAddress.equals("NULL")) {
                openSelectDeviceDialog();
            } else {
                openBtManager(btAddress);
            }
        }
        informTabsAboutNewManager(btManager);
        footerDisplayManager.setBTManager(btManager);

    }

    private void informTabsAboutNewManager(ISerialConnectionManager manager) {
        for(int i = 0; i < viewPagerAdapter.getCount(); i++) {
            KMEViewerTab tab = (KMEViewerTab) viewPagerAdapter.getItem(i);
            tab.setBtManager(manager);
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        closeBtManager();
        super.onPause();
    }

    private void openSelectDeviceDialog() {
        if (!CheckIfBtAdapterExist()) return;
        DeviceListDialog deviceListDialog = new DeviceListDialog(this);
        deviceListDialog.setTitle(R.string.menu_deviceSelect);
        deviceListDialog.setOnDeviceSelectedCallback(this);
        deviceListDialog.show();
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        if (device != null) {
            btAddress = device.getAddress();
            prefs.edit().putString(DEVICE_PREF_NAME, btAddress).apply();
            openBtManager(btAddress);
            footerDisplayManager.setBTManager(btManager);
            viewPagerAdapter.getItem(viewPager.getCurrentItem()).onResume();
        }
    }

    private void closeBtManager() {
        if (footerDisplayManager != null) footerDisplayManager.closeManager();
        if (btManager != null) btManager.stopConnections();
    }

    private void openBtManager(String btAddress) {
        closeBtManager();
        if (!CheckIfBtAdapterExist()) return;
        btManager = new BluetoothConnectionManager(btAddress);
        btManager.startConnecting();
        informTabsAboutNewManager(btManager);
    }

    private void openDemoManager() {
        closeBtManager();
        btManager = new NullConnectionManager();
        btManager.startConnecting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STAY_ON_KEY_NAME, stayScreenOnEnabled);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem switchModeItem = menu.findItem(R.id.action_SwitchColorMode);
        switchModeItem.setTitle(darkMode ? R.string.menu_switchcolor_day
                : R.string.menu_switchcolor_dark);
        MenuItem stayOnItem = menu.findItem(R.id.action_StayScreenOn);
        stayOnItem.setChecked(stayScreenOnEnabled);
        return true;
    }

    // Workaround for not showed icons on Android 3.0+
    // http://stackoverflow.com/questions/19750635/icon-in-menu-not-showing-in-android
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(Exception e) {
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
                openSelectDeviceDialog();
                return true;
            case R.id.action_StayScreenOn:
                item.setChecked(!item.isChecked());
                setStayScreenOn(item.isChecked());
                return true;
            case R.id.action_About:
                TypedValue typedValue = new TypedValue();
                getTheme().resolveAttribute(R.attr.progress_dialog_style, typedValue, true);
                AboutDialog about = new AboutDialog(this, typedValue.resourceId);
                about.setTitle(R.string.about_title);
                about.show();
                return true;
            case R.id.action_SwitchColorMode:
                switchTheme();
                return true;
            case android.R.id.home:
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setStayScreenOn(boolean stayOn) {
        getWindow().getDecorView().getRootView().setKeepScreenOn(stayOn);
        stayScreenOnEnabled = stayOn;
    }

    private void switchTheme() {
        prefs.edit().putBoolean(DARK_MODE_PREF_NAME, !prefs.getBoolean(DARK_MODE_PREF_NAME, true)).commit();
        recreate();
    }

    @Subscribe
    public void onEvent(final SerialConnectionStatusEvent config) {
        footerDisplayManager.setConnectionStateText(config.currentStatus);
    }

    private boolean CheckIfBtAdapterExist() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this);
            alertDialogBuilder.setTitle(R.string.no_bt_adapter);
            alertDialogBuilder.setMessage(R.string.no_bt_adapter_sorry);
            alertDialogBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dial = alertDialogBuilder.create();
            dial.show();
            return false;
        }
        return CheckIfBtAdapterIsEnabled();
    }

    private boolean CheckIfBtAdapterIsEnabled() {
        if (btAdapter == null)
            return false;
        if (!doBTEnabledCheck) return true;
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_BT_ENABLE) {
            if (resultCode == RESULT_CANCELED) {
                // FIXME: For some reason we've got RESULT_CANCELLED when user has defined address
                // and Bluetooth wasn't turned on. Can we get ENABLE_BLUETOOTH result twice?
                // Need to be checked.
                closeBtManager();
                // TODO: I have no idea how to solve this in better way :(
                doBTEnabledCheck = false;
                showBtMustBeEnabledDialog();
                return;
            }
            // TODO: Fix this, it shoulnt be blocked in this way
            while (btAdapter.getState() != BluetoothAdapter.STATE_ON);
            openBtManager(btAddress);
            return;
        }
    }

    private void showBtMustBeEnabledDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.cannot_run_without_bluetooth)
            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            })
            .show();
    }
}
