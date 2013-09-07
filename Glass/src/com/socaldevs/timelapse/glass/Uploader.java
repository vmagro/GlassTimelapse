package com.socaldevs.timelapse.glass;

import android.os.AsyncTask;
import android.util.Log;

public class Uploader extends AsyncTask<byte[], Void, Void>{

	@Override
	protected Void doInBackground(byte[]... bytes) {
		Log.i("uploader", "starting upload");
		return null;
	}

}
