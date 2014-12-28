package com.mobrembski.kmeviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final int REQUEST_DISCOVERY = 0x1;
	private BluetoothController btcntrl;
    SharedPreferences prefs;
    KMEDataActual dtn = new KMEDataActual();

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    EditText tv = (EditText)findViewById(R.id.editText1);
                    dtn = dtn.GetDataFromByteArray((int[])msg.obj);
                    tv.setText(String.valueOf(dtn.TPS));
                    tv = (EditText)findViewById(R.id.editTextActuator);
                    tv.setText(String.valueOf(dtn.actuator));
                    tv = (EditText)findViewById(R.id.editTextActualTemp);
                    tv.setText(String.valueOf(dtn.actualTemp));
                    break;
                case 2:
                    TextView received = (TextView)findViewById(R.id.receivedCount);
                    Long rec = (Long)msg.obj;
                    received.setText(String.valueOf(rec));
                    break;
                case 3:
                    TextView errors = (TextView)findViewById(R.id.errorsCount);
                    Long err = (Long)msg.obj;
                    errors.setText(String.valueOf(err));
                    break;
            }

        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        prefs = this.getSharedPreferences("com.mobrembski.kmeviewer", Context.MODE_PRIVATE);
		setContentView(R.layout.activity_main);
        String address = prefs.getString("com.mobrembski.kmeviewer.Device", "00:12:6F:2E:8A:03");
        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        if(device!=null) {
            btcntrl = new BluetoothController(device, mHandler);
            btcntrl.Start();
        }
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
            btcntrl = new BluetoothController(device, mHandler);
            btcntrl.Start();
            prefs.edit().putString("com.mobrembski.kmeviewer.Device",device.getAddress()).apply();
        }
        return;
		
	}
	
	private class MyListener implements KMEDataChanged {

		@Override
		public void onDataChanged(final KMEDataActual data) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){
				public void run(){
					EditText tv = (EditText)findViewById(R.id.editText1);
					tv.setText(Float.toString(data.TPS));
				}
			});

		}
		
	}

}
