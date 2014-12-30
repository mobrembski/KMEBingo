package com.mobrembski.kmeviewer;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends FragmentActivity implements Observer {

    private static final int REQUEST_DISCOVERY = 0x1;
	private BluetoothController btcntrl;
    SharedPreferences prefs;
    KMEViewerTab actualParametersFragment;

    ActionBar.Tab actualParamTab,infoTab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        prefs = this.getSharedPreferences("com.mobrembski.kmeviewer", Context.MODE_PRIVATE);
        String address = prefs.getString("com.mobrembski.kmeviewer.Device", "00:12:6F:2E:8A:03");
        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        btcntrl = new BluetoothController(device);
        btcntrl.addObserver(this);
        ActionBar actionBar = getActionBar();

        // Screen handling while hiding ActionBar icon.
        actionBar.setDisplayShowHomeEnabled(false);

        // Screen handling while hiding Actionbar title.
        actionBar.setDisplayShowTitleEnabled(false);

        // Creating ActionBar tabs.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actualParametersFragment = new ActualParametersTab();
        KMEViewerTab kmeInfoFragment = new KmeInfoTab();
        actualParamTab = actionBar.newTab();
        actualParamTab.setText("ActualParam");
        infoTab = actionBar.newTab();
        infoTab.setText("Info");
        actualParamTab.setTabListener(new TabListener(actualParametersFragment,btcntrl));
        infoTab.setTabListener(new TabListener(kmeInfoFragment,btcntrl));
        actualParametersFragment.setController(btcntrl);
        kmeInfoFragment.setController(btcntrl);
        btcntrl.addObserver(actualParametersFragment);
        // Setting tab listeners.
        // Adding tabs to the ActionBar.
        actionBar.addTab(actualParamTab);
        actionBar.addTab(infoTab);


        btcntrl.Start();
	}

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
                if(btcntrl!=null)
                    btcntrl.Stop();
                Intent intent = new Intent(this, DiscoveryActivity.class);
                Toast.makeText(this, "select device to connect", Toast.LENGTH_SHORT).show();
                startActivityForResult(intent, REQUEST_DISCOVERY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	protected void onDestroy() {
		if(btcntrl!=null)
			btcntrl.Stop();
		super.onDestroy();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode != REQUEST_DISCOVERY) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
		final BluetoothDevice device = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if(device!=null) {
            btcntrl = new BluetoothController(device);
            btcntrl.addObserver(this);
            btcntrl.addObserver(actualParametersFragment);
            btcntrl.Start();
            prefs.edit().putString("com.mobrembski.kmeviewer.Device",device.getAddress()).apply();
        }
	}

    @Override
    public void update(Observable observable, Object o) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /*EditText tv = (EditText)findViewById(R.id.editText1);
                KMEDataActual dtn = btcntrl.GetActualParameters();
                tv.setText(String.valueOf(dtn.TPS));
                tv = (EditText)findViewById(R.id.editTextActuator);
                tv.setText(String.valueOf(dtn.actuator));
                tv = (EditText)findViewById(R.id.editTextActualTemp);
                tv.setText(String.valueOf(dtn.actualTemp));*/
            }
        });
    }

}
