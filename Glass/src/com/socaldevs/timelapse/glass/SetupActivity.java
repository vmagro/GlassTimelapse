package com.socaldevs.timelapse.glass;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

public class SetupActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);
		
		SharedPreferences prefs = this.getSharedPreferences("stor",
				Context.MODE_PRIVATE);
		if (!prefs.getBoolean("paired", false)) {
			goToMain();
		}
	}

	private void send() {
		new SendUUID().execute(Settings.Secure.getString(getContentResolver(),
				Secure.ANDROID_ID));
		Log.i("uuid", Secure.getString(getContentResolver(), Secure.ANDROID_ID));
	}

	private static BluetoothAdapter adapter = BluetoothAdapter
			.getDefaultAdapter();

	public class SendUUID extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			String id = params[0] + "\n";
			
			BluetoothDevice device = adapter.getBondedDevices().iterator().next();
			BluetoothSocket socket = null;
			OutputStream output = null;
			try {
				socket = device
						.createRfcommSocketToServiceRecord(Constants.BT_UUID);
				output = socket.getOutputStream();
				socket.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (socket != null) {
				try {
					output.write(id.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			} else {
				Log.e("status", "socket is null");
				return false;
			}
			Log.i("send uuid", "wrote bytes");

			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				SharedPreferences prefs = getSharedPreferences("stor", Context.MODE_PRIVATE);
				prefs.edit().putBoolean("paired", true).commit();
				goToMain();
			} else {
				SharedPreferences prefs = getSharedPreferences("stor", Context.MODE_PRIVATE);
				prefs.edit().putBoolean("paired", false).commit();
				setError();
			}
		}

	}
	
	public void goToMain(){
		Intent i = new Intent(this, MainActivity.class);
		startActivity(i);
	}

	public void setError() {
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				TextView tv = (TextView) SetupActivity.this
						.findViewById(R.id.setup_err_text);
				tv.setText("Error, please try again");
			}

		});
	}

	/**
	 * Handle the tap event from the touchpad.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// On Tap, we want to open the menus
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			send();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

}
