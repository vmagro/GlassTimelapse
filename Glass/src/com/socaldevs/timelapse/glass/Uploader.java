package com.socaldevs.timelapse.glass;

import java.io.IOException;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

public class Uploader extends AsyncTask<byte[], Void, Void>{
	
	private static BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
	
	private static BluetoothSocket socket = null;
	private static OutputStream output = null;
	
	static{
		BluetoothDevice device = adapter.getBondedDevices().iterator().next();
		try {
			socket = device.createRfcommSocketToServiceRecord(Constants.BT_UUID);
			output = socket.getOutputStream();
			socket.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int index = 0;
	
	public Uploader(int id){
		this.index = id;
	}

	@SuppressLint("NewApi")
	@Override
	protected Void doInBackground(byte[]... bytes) {
		Log.i("uploader", "starting upload");
		
		Log.i("socket connected", String.valueOf(socket.isConnected()));
		
		if(socket != null){
			try {
				output.write(bytes[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			Log.e("status", "socket is null");
		}
		Log.i("uploader", "wrote bytes");
		
		return null;
	}

}
