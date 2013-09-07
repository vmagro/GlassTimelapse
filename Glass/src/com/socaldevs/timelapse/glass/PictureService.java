package com.socaldevs.timelapse.glass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PictureService extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent i) {
		Log.i("service", "received");
	}

}
