package com.socaldevs.timelapse.glass;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
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
			
			
			HttpClient cli = new DefaultHttpClient();
			
			HttpPost post = new HttpPost(Constants.UPLOAD_URL);
			ByteArrayEntity entity = new ByteArrayEntity(bytes);
			post.setEntity(entity);
			
			cli.execute(post);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
