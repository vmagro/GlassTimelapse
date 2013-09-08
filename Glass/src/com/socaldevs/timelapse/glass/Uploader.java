package com.socaldevs.timelapse.glass;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings.Secure;

public class Uploader extends AsyncTask<byte[], Void, Void> {

	private int index = 0;
	private int eventId = 0;
	private Context ctx;

	public Uploader(Context ctx, int eventId, int index) {
		this.ctx = ctx;
		this.eventId = eventId;
		this.index = index;
	}

	@Override
	protected Void doInBackground(byte[]... params) {
		String id = Secure.getString(ctx.getContentResolver(),
				Secure.ANDROID_ID);
		
		try {
			JSONObject json = new JSONObject();
			json.put("glassId", id);
			json.put("eventId", eventId);
			json.put("index", index);
			json.put("image", params[0]);
			
			byte[] bytes = json.toString().getBytes();
			
			HttpURLConnection conn = (HttpURLConnection) new URL(
					Constants.UPLOAD_URL).openConnection();
			
			conn.setRequestMethod("POST");
			conn.getOutputStream().write(bytes);
			conn.getOutputStream().close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
